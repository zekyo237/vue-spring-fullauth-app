<template>
  <div class="card">
    <h1 class="card__title" v-if="mode === 'login'">Login</h1>
    <h1 class="card__title" v-else>Register</h1>
    
    <p class="card__subtitle" v-if="mode === 'login'">Don't you have an account yet?
      <span class="card__action" @click="switchToCreateAccount()">Create an account</span></p>
    <p class="card__subtitle" v-else>Do you already have an account?
      <span class="card__action" @click="switchToLogin()">Login</span></p>
    
    <div class="form-row">
      <input v-model="email" class="form-row__input" type="email" placeholder="Email"/>
    </div>
    <div class="form-row" v-if="mode === 'register'">
      <input v-model="firstname" class="form-row__input" type="text" placeholder="firstname"/>
      <input v-model="lastname" class="form-row__input" type="text" placeholder="lastname"/>
      <input v-model="nickname" class="form-row__input" type="text" placeholder="nickname"/>
    </div>

    <div class="form-row">
      <input v-model="password" class="form-row__input" type="password" placeholder="password"/>
    </div>

    <div class="form-row error" v-if="mode === 'login' && updateStatus === 'error_login'">
      Invalid email address and/or password
    </div>
    <div class="form-row error" v-if="mode === 'register' && updateStatus === 'error_create'">
      Email address already in use
    </div>
    <div class="form-row">
      <button @click="login()" class="button" :class="{'button--disabled' : !validatedFields}" v-if="mode === 'login'">
        <span v-if="updateStatus === 'loading'">Connection in progress...</span>
        <span v-else>Login</span>
      </button>
      <button @click="createAccount()" class="button" :class="{'button--disabled' : !validatedFields}" v-else>
        <span v-if="updateStatus === 'loading'">Creation in progress...</span>
        <span v-else>Register</span>
      </button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Login',
  data: function () {
    return {
      mode: 'login',
      email: '',
      firstname: '',
      lastname: '',
      nickname: '',
      password: '',
    }
  },
  computed:{
    validatedFields: function () {
      if(this.mode === 'register'){
        return !!(this.email !== '' && this.firstname !== '' && this.lastname !== '' && this.nickname && this.password !== '');
      } else {
        return this.email !== '' && this.password !== '';
      }
    },
    updateStatus: function (){
      return this.$store.state.status
    },
  },
  mounted() {
    if(this.$store.state.user.id !== -1){
      this.$router.push('/profile')
    }
  },
  methods: {
    switchToCreateAccount(){
      this.mode = 'register'
    },
    switchToLogin(){
      this.mode = 'login'
    },
    createAccount(){
      const self = this;
      this.$store.dispatch('createAccount' , {
        email : this.email,
        firstname : this.firstname,
        lastname : this.lastname,
        nickname : this.nickname,
        password : this.password
      }).then( (response) => {
        console.log(response)
        self.login()
      }).catch( (error) => {
        console.log(error)
      })
    },
    login(){
      const self = this;
      this.$store.dispatch('login' , {
        email : this.email,
        password : this.password
      }).then( (response) => {
        console.log(response)
        self.$router.push('profile')
      }).catch( (error) => {
        console.log(error)
      })
    },
  }
}

</script>

<style scoped>
  .form-row {
    display: flex;
    margin: 16px 0px;
    gap:16px;
    flex-wrap: wrap;
  }

  .form-row__input {
    padding:8px;
    border: none;
    border-radius: 8px;
    background:#f2f2f2;
    font-weight: 500;
    font-size: 16px;
    flex:1;
    min-width: 100px;
    color: black;
  }

  .form-row__input::placeholder {
    color:#aaaaaa;
  }

  .error{
    color: red;
  }
</style>>
