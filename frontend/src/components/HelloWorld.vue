<template>
  <div class="hello">
    <h1>{{ msg }}</h1>

    <button class="Search__button" @click="callRestService()">
      Call Spring Boot REST backend
    </button>

    <h3>List of images</h3>
    <select name="select" id="select_image" v-on:change="downloadService()">
      <option v-for="(image, id) in response" :key="id">
        {{ image }}
      </option>
    </select>
  </div>
</template>

<script>
import axios from 'axios'
var imageUrl = 'null'
var imageEl = null
export default {
  name: 'save',
  props: {
    msg: String
  },
  data () {
    return {
      response: [],
      errors: []
    }
  },
  methods: {
    downloadService () {
      var x = document.getElementById('select_image').value
      imageUrl = axios.get('/images/', x)
      axios.get(imageUrl, { responseType: 'blob' }).then(function (response) {
        var reader = new window.FileReader()
        reader.readAsDataURL(response.data)
        reader.onload = function () {
          var imageDataUrl = reader.result
          imageEl.setAttribute('src', imageDataUrl)
        }
      })
    },
    callRestService () {

    }
  },
  mounted () {
    axios
      .get('images')
      .then(response => {
        // JSON responses are automatically parsed.
        this.response = response.data
      })
      .catch(e => {
        this.errors.push(e)
      })
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h3 {
  margin: 40px 0 0;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  display: inline-block;
  margin: 0 10px;
}
a {
  color: #42b983;
}
</style>
