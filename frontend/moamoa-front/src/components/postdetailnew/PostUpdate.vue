<template>
  <div class="container">
    <div class="form-section">
      <form @submit.prevent="updatePost" class="post-form">
        <div class="row">
          <div class="col-25">
            <label for="title">상품 이름:</label>
          </div>
          <div class="col-75">
            <input type="text" id="title" v-model="post.title" required>
          </div>
        </div>

        <div class="row">
          <div class="col-25">
            <label for="price">가격:</label>
          </div>
          <div class="col-75">
            <input type="number" id="price" v-model="post.price" required>
          </div>
        </div>

        <div class="row">
          <div class="col-25">
            <label for="headCount">참여 인원:</label>
          </div>
          <div class="col-75">
            <input type="number" id="headCount" v-model="post.headCount" required>
          </div>
        </div>

        <div class="row">
          <div class="col-25">
            <label for="deadLine">마감 일자:</label>
          </div>
          <div class="col-75">
            <input type="datetime-local" id="deadLine" v-model="post.deadLine" required>
          </div>
        </div>

        <div class="row">
          <div class="col-25">
            <label for="receivePlace">수령 장소:</label>
          </div>
          <div class="col-75">
            <input type="text" id="receivePlace" v-model="post.receivePlace" required>
          </div>
        </div>

        <div class="row">
          <div class="col-25">
            <label for="productUrl">제품 URL:</label>
          </div>
          <div class="col-75">
            <input type="url" id="productUrl" v-model="post.productUrl" required>
          </div>
        </div>

        <!-- 해시태그 입력 폼 -->
        <div class="row">
          <div class="col-25">
            <label for="hashtagList">해시태그:</label>
          </div>
          <div class="col-75">
            <input type="text" id="hashtagList" v-model="hashtags" required>
            <!-- 기존에 작성된 해시태그 표시 -->
            <span v-for="(hashtag, index) in post.hashtagList" :key="index">#{{ hashtag }} </span>
          </div>
        </div>


        <div class="row">
          <p class="hint">여러 개의 해시태그를 입력할 경우 쉼표(,)로 구분해주세요.</p>
        </div>

        <div class="row">
          <input type="submit" value="수정 완료" class="submit-button">
        </div>
      </form>
    </div>
    

    <div class="margin-bottom"></div>
  </div>
</template>


 <script setup>
 import axios from 'axios'
 import { ref, onMounted } from 'vue'
 import { useRoute, useRouter } from 'vue-router';
 
 const route = useRoute(); // 현재 라우터 정보를 가져옴
 const router = useRouter();
 
 const postno = route.params.postno;
 
 const post = ref({
   title: '',
   price: null,
   headCount: null,
   deadLine: '',
   receivePlace: '',
   productUrl: '',
   hashtagList: [] // 해시태그 리스트 추가
 })
 
 const hashtags = ref('');
 
 const updatePost = async () => {
  try {
    const accessToken = localStorage.getItem('accessToken')
    const response = await axios.post(
        `http://localhost:8080/api/post/${postno}`,
        {
          title: post.value.title,
          price: post.value.price,
          headCount: post.value.headCount,
          deadLine: post.value.deadLine,
          receivePlace: post.value.receivePlace,
          productUrl: post.value.productUrl,
          hashtagList: parseHashtags(hashtags.value)
        },
        {
          headers: {
            Authorization: `Bearer ${accessToken}`
          }
        }
    )
    console.log('게시글 수정 완료:', response.data);
    alert('게시글 수정이 완료 되었습니다!');
    router.push({ name: 'postno' }); // 작성 완료 후 HomeView로 이동
  } catch (error) {
    console.error('게시글 수정 에러:', error)
  }
 }
 
 // 해시태그를 쉼표(,)로 분할하여 배열로 반환하는 함수
 const parseHashtags = (hashtagsStr) => {
   return hashtagsStr.split(',').map(hashtag => hashtag.trim());
 }
 
 // 게시글 정보를 가져와서 입력 폼에 표시하는 함수
 const loadPost = async () => {
   try {
     const accessToken = localStorage.getItem('accessToken')
     const response = await axios.get(`http://localhost:8080/api/post/${postno}`, {
       headers: {
         Authorization: `Bearer ${accessToken}`
       }
     });
     const postData = response.data;
     post.value.title = postData.title;
     post.value.price = postData.price;
     post.value.headCount = postData.headCount;
     post.value.deadLine = postData.deadLine;
     post.value.receivePlace = postData.receivePlace;
     post.value.productUrl = postData.productUrl;
     post.value.hashtagList = postData.hashtagList.map(tag => tag.hashtag); // 해시태그 리스트 설정
   } catch (error) {
     console.error('게시글 정보 로드 에러:', error)
   }
 }
 
 onMounted(() => {
   loadPost(); // 페이지가 마운트되면 게시글 정보를 로드하여 입력 폼에 표시
 });

</script>

<style scoped>
.container {
  padding: 20px;
}

.header {
  margin-bottom: 20px;
}

.form-section {
  margin-bottom: 20px;
}

.margin-bottom {
  margin-bottom: 20px;
}

.row {
  display: flex;
  margin-bottom: 15px;
}

.col-25 {
  flex: 25%;
}

.col-75 {
  flex: 75%;
}

/* Clear floats after the columns */
.row::after {
  content: "";
  clear: both;
  display: table;
}

input[type=text],
input[type=number],
input[type=datetime-local],
input[type=url],
textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #ccc;
  border-radius: 4px;
  resize: vertical;
}

label {
  padding: 12px 12px 12px 0;
  display: inline-block;
}

input[type=submit] {
  background-color: #04AA6D;
  color: white;
  padding: 12px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  float: right;
}

input[type=submit]:hover {
  background-color: #45a049;
}

.hint {
  font-size: 0.8rem;
  margin-top: 5px;
}
</style>
