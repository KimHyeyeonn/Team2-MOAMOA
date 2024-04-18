<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from "axios";

const router = useRouter()

const accessToken = localStorage.getItem('accessToken')
// JWT 토큰을 점으로 구분된 세 부분으로 분리합니다.
const payload = accessToken.split('.').at(1);
// base64로 인코딩된 payload를 디코딩합니다.
const decodedPayload = JSON.parse(atob(payload));

const bytes = Uint8Array.from(decodedPayload.nickname, c => c.charCodeAt(0));
const decoder = new TextDecoder('utf-8');

const nickname = ref('');
const modalVisible = ref(false);
const logout = async () => {
  const accessToken = localStorage.getItem('accessToken');
  console.log(accessToken);

  const response = await axios.get("http://localhost:8080/api/auth/logout", {
    headers: {
      Authorization: `Bearer ${accessToken}`
    }
  });

  console.log(response);
  // 로그아웃 후 로그인 페이지로 이동
  localStorage.removeItem('accessToken');
  localStorage.removeItem('refreshToken');
  router.push({ name: 'login' })
}

const toSchoolAuth = () => {
  router.push({name: 'auth'})
}

const withdrawMember = () => {
  // 사용자로부터 비밀번호를 입력받음
  var password = prompt("회원 탈퇴를 위해 비밀번호를 입력하세요:");
  const accessToken = localStorage.getItem('accessToken');
  // 사용자가 취소 버튼을 누르거나 아무것도 입력하지 않은 경우
  if (password === null || password === "") {
    alert("비밀번호를 입력해야 합니다.");
  } else {
    // 비밀번호를 서버로 전송하고 회원 탈퇴 작업 수행
    axios.post("http://localhost:8080/api/auth/withdrawal", { password }, {
      headers: {
        Authorization: `Bearer ${accessToken}`
      }
    })
        .then(response => {
          console.log(response.data);
          // 회원 탈퇴 성공 시 로그아웃 처리 등을 수행
          logout();
        })
        .catch(error => {
          console.error(error);
          alert("회원 탈퇴에 실패했습니다. 비밀번호를 확인해주세요.");
        });
  }
}
nickname.value = decoder.decode(bytes);
const openModal = () => {
  modalVisible.value = true;
}

const closeModal = () => {
  modalVisible.value = false;
}

const changePassword = () => {
  // 비밀번호 재설정 로직 구현
  console.log('비밀번호 재설정');
  router.push({name: 'mypagePasswordChange'})
  closeModal();
}

const changeNickname = () => {
  // 닉네임 변경 로직 구현
  console.log('닉네임 변경');
  router.push({name: 'mypageNicknameChange'})
  closeModal();
}


</script>

<template>
  <div class="left-content">
    <div>
      <img src="/userprofile/userdefault.jpg">
    </div>
    <div>
      {{ nickname }}
    </div>

    <div>
      <button @click="openModal" class="action-btn">회원 정보 수정</button>
    </div>

    <div>
      <button @click="withdrawMember" class="action-btn" style="background-color: #df4d1c;">회원 탈퇴</button>
    </div>

    <template v-if="!isAuthencated">
      <button type="button" @click="toSchoolAuth" class="school-auth-btn">학교 인증이요ㅋ</button>
    </template>
    <!-- 모달 -->
    <div v-if="modalVisible" class="modal-mask">
      <div class="modal-wrapper">
        <div class="modal-container">
          <div class="modal-header">
            <h3 class="modal-title">원하는 작업을 선택하세요</h3>
            <button class="modal-close-btn" @click="closeModal">X</button>
          </div>
          <div class="modal-body">
            <button @click="changePassword" class="modal-btn">비밀번호 재설정</button>
            <button @click="changeNickname" class="modal-btn">닉네임 변경</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>


<style scoped>

.left-content img {
  width: 200px;
  height: 200px;
  border-radius: 50%;
  object-fit: cover;
  margin-bottom: 20px; /* 이미지 아래 여백 조정 */
}

.left-content div {
  margin-bottom: 10px; /* 각 요소 사이의 간격 조정 */
}
.modal-title {
  margin-left:8dvw;
  font-weight: bold;
  text-align:center;
}

.modal-mask {
  position: fixed;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index:1000;
}

.modal-wrapper {
  width: 500px;
  background: #fff;
  border-radius: 8px;
  padding: 20px;
}

.modal-header {
  display: flex;
  justify-content: space-between ;
  align-items: center;
  margin-bottom: 10px;
}

.modal-close-btn {
  border: none;
  background: none;
  cursor: pointer;
}

.modal-body {
  display: flex;
  justify-content: space-evenly;
  margin-bottom: 10px;
}

.modal-btn {
  padding: 10px 10px;
  margin: 1dvw;
  border: none;
  border-radius: 5px;
  background-color: #498C74;
  color: #fff;
  font-size: 16px;
  cursor: pointer;
}

.modal-btn:hover {
  background-color: #0056b3;
}
.action-btn {
  padding: 10px 20px;
  margin-right: 10px;
  border: none;
  border-radius: 5px;
  background-color: #fdbcbc;
  color: #fff;
  font-size: 16px;
  cursor: pointer;
  box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
  transition: background-color 0.3s ease;
}



.action-btn:hover {
  background-color: #0056b3;
}


.school-auth-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  background-color: #fdbcbc;
  color: #fff;
  font-size: 16px;
  cursor: pointer;
  box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
  transition: background-color 0.3s ease;
}

.school-auth-btn:hover {
  background-color: #218838;
}
</style>
