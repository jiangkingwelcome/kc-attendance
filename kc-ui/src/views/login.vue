<template>
  <div class="login">
    <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
      <h3 class="title">考勤管理平台</h3>
      <div class="login-quote">{{ currentQuote }}</div>
      <el-form-item prop="username">
        <el-input
          v-model="loginForm.username"
          type="text"
          auto-complete="off"
          placeholder="账号"
        >
          <svg-icon slot="prefix" icon-class="user" class="el-input__icon input-icon" />
        </el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input
          v-model="loginForm.password"
          type="password"
          auto-complete="off"
          placeholder="密码"
          @keyup.enter.native="handleLogin"
        >
          <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
        </el-input>
      </el-form-item>
      <el-form-item prop="code" v-if="captchaEnabled">
        <el-input
          v-model="loginForm.code"
          auto-complete="off"
          placeholder="验证码"
          style="width: 63%"
          @keyup.enter.native="handleLogin"
        >
          <svg-icon slot="prefix" icon-class="validCode" class="el-input__icon input-icon" />
        </el-input>
        <div class="login-code">
          <img :src="codeUrl" @click="getCode" class="login-code-img"/>
        </div>
      </el-form-item>
      <el-checkbox v-model="loginForm.rememberMe" style="margin:0px 0px 25px 0px;">记住密码</el-checkbox>
      <el-form-item style="width:100%;">
        <el-button
          :loading="loading"
          size="medium"
          type="primary"
          style="width:100%;"
          @click.native.prevent="handleLogin"
        >
          <span v-if="!loading">登 录</span>
          <span v-else>登 录 中...</span>
        </el-button>
        <div style="float: right;" v-if="register">
          <router-link class="link-type" :to="'/register'">立即注册</router-link>
        </div>
      </el-form-item>
    </el-form>

  </div>
</template>

<script>
import { getCodeImg } from "@/api/login";
import Cookies from "js-cookie";
import { encrypt, decrypt } from '@/utils/jsencrypt'

export default {
  name: "Login",
  data() {
    return {
      codeUrl: "",
      currentQuote: "",
      loginForm: {
        username: "admin",
        password: "123456",
        rememberMe: false,
        code: "",
        uuid: ""
      },
      loginRules: {
        username: [
          { required: true, trigger: "blur", message: "请输入您的账号" }
        ],
        password: [
          { required: true, trigger: "blur", message: "请输入您的密码" }
        ],
        code: [{ required: true, trigger: "change", message: "请输入验证码" }]
      },
      loading: false,
      // 验证码开关
      captchaEnabled: true,
      // 注册开关
      register: false,
      redirect: undefined
    };
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect;
      },
      immediate: true
    }
  },
  created() {
    this.getQuote();
    this.getCode();
    this.getCookie();
  },
  methods: {
    getQuote() {
      const quotes = [
        "感谢你的付出，让快仓变得更好",
        "每一份努力，都值得被记录",
        "智造未来，快人一步",
        "你的每一分耕耘，都在点亮未来",
        "高效协同，成就非凡",
        "新的一天，遇见更好的自己",
        "保持热爱，奔赴山海",
        "因为有你，我们走得更远",
        "致敬每一位奋斗者",
        "卓越源于你的坚持与专注"
      ];
      this.currentQuote = quotes[Math.floor(Math.random() * quotes.length)];
    },
    getCode() {
      getCodeImg().then(res => {
        this.captchaEnabled = res.captchaEnabled === undefined ? true : res.captchaEnabled;
        if (this.captchaEnabled) {
          this.codeUrl = "data:image/gif;base64," + res.img;
          this.loginForm.uuid = res.uuid;
        }
      });
    },
    getCookie() {
      const username = Cookies.get("username");
      const password = Cookies.get("password");
      const rememberMe = Cookies.get('rememberMe')
      this.loginForm = {
        username: username === undefined ? this.loginForm.username : username,
        password: password === undefined ? this.loginForm.password : decrypt(password),
        rememberMe: rememberMe === undefined ? false : Boolean(rememberMe)
      };
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true;
          if (this.loginForm.rememberMe) {
            Cookies.set("username", this.loginForm.username, { expires: 30 });
            Cookies.set("password", encrypt(this.loginForm.password), { expires: 30 });
            Cookies.set('rememberMe', this.loginForm.rememberMe, { expires: 30 });
          } else {
            Cookies.remove("username");
            Cookies.remove("password");
            Cookies.remove('rememberMe');
          }
          this.$store.dispatch("Login", this.loginForm).then(() => {
            this.$router.push({ path: this.redirect || "/" }).catch(()=>{});
          }).catch(() => {
            this.loading = false;
            if (this.captchaEnabled) {
              this.getCode();
            }
          });
        }
      });
    }
  }
};
</script>

<style rel="stylesheet/scss" lang="scss">
.login {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  background-image: url("../assets/images/login-background.jpg");
  background-size: cover;
}
.title {
  margin: 0px auto 15px auto;
  text-align: center;
  color: #3875F6;
  font-weight: 600;
  font-size: 24px;
}

.login-quote {
  margin: 0px auto 30px auto;
  text-align: center;
  color: #555; // Slightly darker for legibility
  font-size: 14px;
  font-weight: 500; // Slightly bolder
  letter-spacing: 1px;
  font-style: italic; // Add style
  opacity: 0.8;
  transition: all 0.5s;
  
  &:hover {
      opacity: 1;
      transform: scale(1.02);
      color: #3875F6; // Brand color on hover
  }
}

.login-form {
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.85); // Semi-transparent white
  backdrop-filter: blur(12px); // Glass blur effect
  -webkit-backdrop-filter: blur(12px);
  width: 420px;
  padding: 30px 40px;
  box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.15); // Soft deep shadow
  border: 1px solid rgba(255, 255, 255, 0.18); // Subtle border
  
  transition: transform 0.3s ease;
  &:hover {
      transform: translateY(-2px); // Subtle float on hover
  }
  .el-button--primary {
      background: linear-gradient(90deg, #3875F6 0%, #2563EB 100%);
      border: none;
      height: 40px;
      font-size: 16px;
      letter-spacing: 4px;
      border-radius: 8px;
      box-shadow: 0 4px 12px rgba(56, 117, 246, 0.3);
      transition: all 0.3s;

      &:hover {
          background: linear-gradient(90deg, #2563EB 0%, #1D4ED8 100%);
          transform: translateY(-1px);
          box-shadow: 0 6px 16px rgba(56, 117, 246, 0.4);
      }
      
      &:active {
          transform: translateY(1px);
      }
  }

  .el-input {
    height: 38px;
    input {
      height: 38px;
    }
  }
  .input-icon {
    height: 39px;
    width: 14px;
    margin-left: 2px;
  }
}
.login-tip {
  font-size: 13px;
  text-align: center;
  color: #bfbfbf;
}
.login-code {
  width: 33%;
  height: 38px;
  float: right;
  img {
    cursor: pointer;
    vertical-align: middle;
  }
}

.login-code-img {
  height: 38px;
}
</style>
