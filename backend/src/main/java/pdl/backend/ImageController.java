package pdl.backend;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
public class ImageController {

  @Autowired
  private ObjectMapper mapper;
  private final ImageDao imageDao;

  @Autowired
  public ImageController(ImageDao imageDao) {
    this.imageDao = imageDao;
  }

  @RequestMapping(value = "/images/{id}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
  public ResponseEntity<?> getImage(@PathVariable("id") long id) {
    Optional<Image> i = imageDao.retrieve(id);
    if (i.isEmpty()) {
      return new ResponseEntity<>("image not found", HttpStatus.NOT_FOUND);
    }

    return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(i.get().getData());
  }

  @RequestMapping(value = "/images/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<?> deleteImage(@PathVariable("id") long id) {
    Optional<Image> img = imageDao.retrieve(id);
    if (img.isEmpty()) {
      return new ResponseEntity<>("image not found", HttpStatus.NOT_FOUND);
    }
    imageDao.delete(img.get());
    return new ResponseEntity<>("image deleted", HttpStatus.OK);
  }

  @RequestMapping(value = "/images", method = RequestMethod.POST)
  public ResponseEntity<?> addImage(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes)
      throws IOException {
    if (!Objects.equals(MediaType.IMAGE_JPEG.toString(), file.getContentType())) {
      return new ResponseEntity<>("File not in jpeg format", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }
    Image i = new Image(file.getName(), file.getBytes());
    imageDao.create(i);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @RequestMapping(value = "/images", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
  @ResponseBody
  public ArrayNode getImageList() {
    ArrayNode nodes = mapper.createArrayNode();
    imageDao.retrieveAll().forEach(i -> nodes.insert((int) i.getId(), i.getName()));
    return nodes;
  }

}
