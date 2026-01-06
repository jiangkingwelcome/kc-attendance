<template>
  <div class="login">
    <!-- 背景装饰效果 -->
    <div class="background-decoration">
      <div class="gradient-orb orb-1"></div>
      <div class="gradient-orb orb-2"></div>
      <div class="gradient-orb orb-3"></div>
    </div>

    <el-form
      ref="loginForm"
      :model="loginForm"
      :rules="loginRules"
      class="login-form"
      @submit.native.prevent="handleLogin"
    >
      <!-- Logo 和标题区域 -->
      <div class="login-header">
        <div class="logo-wrapper">
          <svg class="logo-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M2 17L12 22L22 17" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M2 12L12 17L22 12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <h1 class="title">快仓考勤管理平台</h1>
        <p class="login-quote">{{ currentQuote }}</p>
      </div>

      <!-- 用户名输入 - 增强可访问性 -->
      <el-form-item prop="username" class="form-item-enhanced">
        <label for="username-input" class="form-label">账号</label>
        <el-input
          id="username-input"
          v-model="loginForm.username"
          type="text"
          autocomplete="username"
          placeholder="请输入您的账号"
          :class="{ 'input-error': usernameError }"
          @focus="handleInputFocus('username')"
          @blur="handleInputBlur('username')"
        >
          <template #prefix>
            <svg class="input-icon" viewBox="0 0 24 24" fill="none">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <circle cx="12" cy="7" r="4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </template>
        </el-input>
      </el-form-item>

      <!-- 密码输入 - 增强可访问性和功能 -->
      <el-form-item prop="password" class="form-item-enhanced">
        <label for="password-input" class="form-label">密码</label>
        <el-input
          id="password-input"
          v-model="loginForm.password"
          :type="showPassword ? 'text' : 'password'"
          autocomplete="current-password"
          placeholder="请输入您的密码"
          :class="{ 'input-error': passwordError }"
          @focus="handleInputFocus('password')"
          @blur="handleInputBlur('password')"
          @keyup.enter.native="handleLogin"
        >
          <template #prefix>
            <svg class="input-icon" viewBox="0 0 24 24" fill="none">
              <rect x="3" y="11" width="18" height="11" rx="2" ry="2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M7 11V7a5 5 0 0 1 10 0v4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </template>
          <template #suffix>
            <button
              type="button"
              class="password-toggle"
              @click="showPassword = !showPassword"
              :aria-label="showPassword ? '隐藏密码' : '显示密码'"
            >
              <svg v-if="!showPassword" class="icon-eye" viewBox="0 0 24 24" fill="none">
                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              <svg v-else class="icon-eye" viewBox="0 0 24 24" fill="none">
                <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <line x1="1" y1="1" x2="23" y2="23" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </button>
          </template>
        </el-input>
      </el-form-item>

      <!-- 验证码 - 增强可访问性 -->
      <el-form-item prop="code" v-if="captchaEnabled" class="form-item-enhanced captcha-item">
        <label for="code-input" class="form-label">验证码</label>
        <div class="captcha-wrapper">
          <el-input
            id="code-input"
            v-model="loginForm.code"
            autocomplete="off"
            placeholder="请输入验证码"
            :class="{ 'input-error': codeError }"
            @focus="handleInputFocus('code')"
            @blur="handleInputBlur('code')"
            @keyup.enter.native="handleLogin"
          >
            <template #prefix>
              <svg class="input-icon" viewBox="0 0 24 24" fill="none">
                <path d="M12 1a3 3 0 0 0-3 3v8a3 3 0 0 0 6 0V4a3 3 0 0 0-3-3z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M19 10v2a7 7 0 0 1-14 0v-2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <line x1="12" y1="19" x2="12" y2="23" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <line x1="8" y1="23" x2="16" y2="23" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </template>
          </el-input>
          <div class="captcha-image-wrapper">
            <img
              :src="codeUrl"
              @click="getCode"
              class="captcha-image"
              alt="验证码图片，点击刷新"
              role="button"
              tabindex="0"
              @keyup.enter="getCode"
            />
          </div>
          <button
            type="button"
            class="refresh-captcha"
            @click="getCode"
            aria-label="刷新验证码"
            title="刷新验证码"
          >
            <svg class="refresh-icon" :class="{ 'spinning': refreshingCaptcha }" viewBox="0 0 24 24" fill="none">
              <path d="M21.5 2v6h-6M2.5 22v-6h6M2 11.5a10 10 0 0 1 18.8-4.3M22 12.5a10 10 0 0 1-18.8 4.2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </button>
        </div>
      </el-form-item>

      <!-- 记住密码选项 -->
      <div class="remember-wrapper">
        <el-checkbox v-model="loginForm.rememberMe" class="remember-checkbox">
          记住密码
        </el-checkbox>
        <a href="#" class="forgot-password" @click.prevent="handleForgotPassword">
          忘记密码？
        </a>
      </div>

      <!-- 错误提示区域 - 增强可访问性 -->
      <div
        v-if="errorMessage"
        class="error-alert"
        role="alert"
        aria-live="assertive"
      >
        <svg class="error-icon" viewBox="0 0 24 24" fill="none">
          <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          <line x1="12" y1="8" x2="12" y2="12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          <line x1="12" y1="16" x2="12.01" y2="16" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        <span>{{ errorMessage }}</span>
      </div>

      <!-- 登录按钮 -->
      <el-form-item class="submit-wrapper">
        <button
          type="submit"
          class="login-button"
          :class="{ 'loading': loading }"
          :disabled="loading"
          @click.prevent="handleLogin"
        >
          <span v-if="!loading" class="button-content">
            <span>登录</span>
            <svg class="arrow-icon" viewBox="0 0 24 24" fill="none">
              <line x1="5" y1="12" x2="19" y2="12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <polyline points="12 5 19 12 12 19" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </span>
          <span v-else class="button-content">
            <svg class="loading-spinner" viewBox="0 0 24 24" fill="none">
              <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" stroke-opacity="0.25"/>
              <path d="M12 2a10 10 0 0 1 10 10" stroke="currentColor" stroke-width="4" stroke-linecap="round"/>
            </svg>
            <span>登录中...</span>
          </span>
        </button>
      </el-form-item>

      <!-- 注册链接 -->
      <div v-if="register" class="register-wrapper">
        <span class="register-text">还没有账号？</span>
        <router-link class="register-link" :to="'/register'">立即注册</router-link>
      </div>
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
      showPassword: false,
      refreshingCaptcha: false,
      errorMessage: "",
      usernameError: false,
      passwordError: false,
      codeError: false,
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
      // 验证码开关 - 临时启用以便测试
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
      this.refreshingCaptcha = true;
      getCodeImg().then(res => {
        // 临时强制启用验证码以便测试
        // this.captchaEnabled = res.captchaEnabled === undefined ? true : res.captchaEnabled;
        this.captchaEnabled = true; // 强制启用

        if (res.img) {
          this.codeUrl = "data:image/gif;base64," + res.img;
          this.loginForm.uuid = res.uuid;
        }
        setTimeout(() => {
          this.refreshingCaptcha = false;
        }, 500);
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
    handleInputFocus(field) {
      this[field + 'Error'] = false;
      this.errorMessage = "";
    },
    handleInputBlur(field) {
      // 可以添加实时验证逻辑
    },
    handleForgotPassword() {
      this.$message.info('请联系管理员重置密码');
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true;
          this.errorMessage = "";

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
          }).catch((error) => {
            this.loading = false;
            const errorMsg = error.message || "登录失败";
            this.errorMessage = errorMsg;

            // 根据错误信息智能标记错误字段
            if (errorMsg.includes("验证码")) {
              // 只标记验证码错误
              this.codeError = true;
              if (this.captchaEnabled) {
                this.getCode();
              }
            } else if (errorMsg.includes("用户名") || errorMsg.includes("账号")) {
              // 只标记用户名错误
              this.usernameError = true;
            } else if (errorMsg.includes("密码")) {
              // 只标记密码错误
              this.passwordError = true;
            } else {
              // 未知错误，标记账号和密码（不标记验证码）
              this.usernameError = true;
              this.passwordError = true;
              if (this.captchaEnabled) {
                this.getCode();
              }
            }
          });
        } else {
          this.errorMessage = "请填写完整的登录信息";
        }
      });
    }
  }
};
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
// 主容器
.login {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 20px;
  background-image: url("../assets/images/login-background.jpg");
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  overflow: hidden;
}

// 背景装饰效果（减弱以配合背景图）
.background-decoration {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
  opacity: 0.3; // 降低整体透明度，不干扰背景图
}

.gradient-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.15; // 进一步降低透明度
  animation: float 20s ease-in-out infinite;

  &.orb-1 {
    width: 400px;
    height: 400px;
    background: radial-gradient(circle, #fbbf24 0%, transparent 70%);
    top: -10%;
    left: -10%;
    animation-delay: 0s;
  }

  &.orb-2 {
    width: 500px;
    height: 500px;
    background: radial-gradient(circle, #ec4899 0%, transparent 70%);
    bottom: -15%;
    right: -15%;
    animation-delay: 7s;
  }

  &.orb-3 {
    width: 350px;
    height: 350px;
    background: radial-gradient(circle, #3b82f6 0%, transparent 70%);
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    animation-delay: 14s;
  }
}

@keyframes float {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(30px, -30px) scale(1.1); }
  66% { transform: translate(-20px, 20px) scale(0.9); }
}

// 登录表单容器
.login-form {
  position: relative;
  width: 100%;
  max-width: 440px;
  padding: 48px 40px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow:
    0 8px 32px rgba(31, 38, 135, 0.15),
    0 2px 8px rgba(0, 0, 0, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.6);

  transition: transform 0.3s ease, box-shadow 0.3s ease;

  &:hover {
    transform: translateY(-4px);
    box-shadow:
      0 12px 48px rgba(31, 38, 135, 0.2),
      0 4px 16px rgba(0, 0, 0, 0.12),
      inset 0 1px 0 rgba(255, 255, 255, 0.6);
  }
}

// Logo 和标题区域
.login-header {
  text-align: center;
  margin-bottom: 36px;
}

.logo-wrapper {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 72px;
  height: 72px;
  margin-bottom: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.3);

  .logo-icon {
    width: 40px;
    height: 40px;
    color: white;
  }
}

.title {
  margin: 0 0 12px 0;
  font-size: 28px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: -0.5px;
}

.login-quote {
  margin: 0;
  padding: 12px 20px;
  font-size: 14px;
  font-weight: 500;
  font-style: italic;
  color: #64748b;
  letter-spacing: 0.3px;
  line-height: 1.6;
  background: linear-gradient(90deg, transparent, rgba(102, 126, 234, 0.08), transparent);
  border-radius: 8px;
  transition: all 0.3s ease;

  &:hover {
    color: #667eea;
    background: linear-gradient(90deg, transparent, rgba(102, 126, 234, 0.12), transparent);
  }
}

// 表单项增强
.form-item-enhanced {
  margin-bottom: 24px;

  .form-label {
    display: block;
    margin-bottom: 8px;
    font-size: 14px;
    font-weight: 600;
    color: #1e293b;
    transition: color 0.2s ease;
  }

  ::v-deep .el-input__inner {
    height: 48px;
    padding-left: 44px;
    padding-right: 44px;
    font-size: 15px;
    border: 2px solid #e2e8f0;
    border-radius: 12px;
    background: white;
    transition: all 0.2s ease;

    &:hover {
      border-color: #cbd5e1;
    }

    &:focus {
      border-color: #667eea;
      box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
      outline: none;
    }

    &::placeholder {
      color: #94a3b8;
    }
  }

  &:focus-within .form-label {
    color: #667eea;
  }
}

// 输入框图标
.input-icon {
  width: 20px;
  height: 20px;
  color: #94a3b8;
  transition: color 0.2s ease;
}

::v-deep .el-input__prefix {
  left: 14px;
  display: flex;
  align-items: center;
}

::v-deep .el-input__suffix {
  right: 14px;
  display: flex;
  align-items: center;
}

// 密码显示切换按钮
.password-toggle {
  padding: 4px;
  background: none;
  border: none;
  cursor: pointer;
  color: #94a3b8;
  transition: color 0.2s ease;

  &:hover {
    color: #667eea;
  }

  &:focus {
    outline: 2px solid #667eea;
    outline-offset: 2px;
    border-radius: 4px;
  }

  .icon-eye {
    width: 20px;
    height: 20px;
  }
}

// 验证码区域
.captcha-item {
  .captcha-wrapper {
    display: flex;
    gap: 8px;
    align-items: flex-start;
  }

  ::v-deep .el-input {
    flex: 1;
  }

  .captcha-image-wrapper {
    position: relative;
    flex-shrink: 0;
    width: 120px;
    height: 48px;
    border-radius: 12px;
    overflow: hidden;
    border: 2px solid #e2e8f0;
    transition: border-color 0.2s ease;

    &:hover {
      border-color: #667eea;
    }
  }

  .captcha-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
    cursor: pointer;
    transition: opacity 0.2s ease;

    &:hover {
      opacity: 0.8;
    }

    &:focus {
      outline: 2px solid #667eea;
      outline-offset: 2px;
    }
  }

  .refresh-captcha {
    flex-shrink: 0;
    width: 48px;
    height: 48px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(102, 126, 234, 0.1);
    border: 2px solid #e2e8f0;
    border-radius: 12px;
    cursor: pointer;
    transition: all 0.2s ease;

    &:hover {
      background: rgba(102, 126, 234, 0.15);
      border-color: #667eea;
      transform: translateY(-1px);
    }

    &:active {
      transform: translateY(0);
    }

    &:focus {
      outline: 2px solid #667eea;
      outline-offset: 2px;
    }

    .refresh-icon {
      width: 20px;
      height: 20px;
      color: #667eea;
      transition: transform 0.3s ease;

      &.spinning {
        animation: spin 0.5s linear;
      }
    }
  }
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

// 记住密码区域
.remember-wrapper {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;

  .remember-checkbox {
    ::v-deep .el-checkbox__label {
      font-size: 14px;
      color: #475569;
    }
  }

  .forgot-password {
    font-size: 14px;
    font-weight: 500;
    color: #667eea;
    text-decoration: none;
    transition: color 0.2s ease;

    &:hover {
      color: #764ba2;
      text-decoration: underline;
    }

    &:focus {
      outline: 2px solid #667eea;
      outline-offset: 2px;
      border-radius: 4px;
    }
  }
}

// 错误提示
.error-alert {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  margin-bottom: 20px;
  background: #fef2f2;
  border: 1px solid #fecaca;
  border-radius: 12px;
  color: #dc2626;
  font-size: 14px;
  animation: slideDown 0.3s ease;

  .error-icon {
    flex-shrink: 0;
    width: 20px;
    height: 20px;
    color: #dc2626;
  }
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

// 输入错误状态
.input-error ::v-deep .el-input__inner {
  border-color: #dc2626 !important;

  &:focus {
    box-shadow: 0 0 0 4px rgba(220, 38, 38, 0.1) !important;
  }
}

// 登录按钮
.submit-wrapper {
  margin-bottom: 20px;
}

.login-button {
  width: 100%;
  height: 52px;
  padding: 0 24px;
  font-size: 16px;
  font-weight: 600;
  color: white;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 12px;
  cursor: pointer;
  box-shadow:
    0 4px 14px rgba(102, 126, 234, 0.4),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
  transition: all 0.2s ease;

  &:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow:
      0 6px 20px rgba(102, 126, 234, 0.5),
      inset 0 1px 0 rgba(255, 255, 255, 0.2);
  }

  &:active:not(:disabled) {
    transform: translateY(0);
  }

  &:focus {
    outline: none;
    box-shadow:
      0 0 0 4px rgba(102, 126, 234, 0.2),
      0 4px 14px rgba(102, 126, 234, 0.4),
      inset 0 1px 0 rgba(255, 255, 255, 0.2);
  }

  &:disabled {
    opacity: 0.7;
    cursor: not-allowed;
  }

  .button-content {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
  }

  .arrow-icon {
    width: 20px;
    height: 20px;
    transition: transform 0.2s ease;
  }

  &:hover:not(:disabled) .arrow-icon {
    transform: translateX(4px);
  }

  .loading-spinner {
    width: 20px;
    height: 20px;
    animation: spin 1s linear infinite;
  }
}

// 注册区域
.register-wrapper {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #e2e8f0;

  .register-text {
    font-size: 14px;
    color: #64748b;
    margin-right: 8px;
  }

  .register-link {
    font-size: 14px;
    font-weight: 600;
    color: #667eea;
    text-decoration: none;
    transition: color 0.2s ease;

    &:hover {
      color: #764ba2;
      text-decoration: underline;
    }

    &:focus {
      outline: 2px solid #667eea;
      outline-offset: 2px;
      border-radius: 4px;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .login-form {
    padding: 36px 24px;
    border-radius: 20px;
  }

  .title {
    font-size: 24px;
  }

  .login-quote {
    font-size: 13px;
    padding: 10px 16px;
  }

  .captcha-item .captcha-wrapper {
    flex-direction: column;

    .captcha-image-wrapper {
      width: 100%;
    }
  }
}

// 减少动画（可访问性）
@media (prefers-reduced-motion: reduce) {
  *,
  *::before,
  *::after {
    animation-duration: 0.01ms !important;
    animation-iteration-count: 1 !important;
    transition-duration: 0.01ms !important;
  }

  .gradient-orb {
    animation: none;
  }
}

// 深色模式支持（可选）
@media (prefers-color-scheme: dark) {
  .login-form {
    background: rgba(30, 41, 59, 0.95);
    border-color: rgba(255, 255, 255, 0.1);

    .form-label {
      color: #e2e8f0;
    }

    ::v-deep .el-input__inner {
      background: rgba(51, 65, 85, 0.5);
      border-color: rgba(148, 163, 184, 0.2);
      color: #e2e8f0;

      &::placeholder {
        color: #64748b;
      }
    }

    .login-quote {
      color: #94a3b8;
    }

    .remember-checkbox ::v-deep .el-checkbox__label {
      color: #cbd5e1;
    }
  }
}
</style>
