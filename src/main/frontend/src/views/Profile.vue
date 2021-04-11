<template>
  <div class="card">
    <h1 class="card__title">My Profile</h1>
    <p class="card__subtitle">So i am ...</p>
    <p><span class="header">Firstname:</span> {{ userInfos.firstname }}</p>
    <p><span class="header">Lastname:</span> {{userInfos.lastname}}</p>
    <p><span class="header">Nickname:</span> {{userInfos.nickname}}</p>

    <h4>Authorities </h4>
    <ul>
      <li :key="index" v-for="(auth, index) in userInfos.grantedAuthorities">
        {{index}} - {{auth}}
      </li>
    </ul>

    <p><span class="header">Email:</span> {{userInfos.email}}</p>
    <img :src="userInfos.image"/>
    <div class="form-row">
      <button class="button" @click="logout">
        Logout
      </button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Profile',
  mounted: function () {
    console.log(this.$store.state.user)

    if(this.$store.state.user.id === -1){
      this.$router.push('/')
    }
    this.$store.dispatch('getUserInfos')
  },
  computed: {
    userInfos : function (){
      return this.$store.state.userInfos
    }
  },
  methods:{
    logout(){
      this.$store.commit('logout');
      this.$router.push("/");
    }
  }
}
</script>

<style scoped>
li{
  list-style: none;
}
.header{
  font-weight: bold;
}
img{
  margin-top: 15px;
  margin-bottom: 15px;
  left: 50%;
  position: relative;
  transform: translateX(-50%);
}
</style>
