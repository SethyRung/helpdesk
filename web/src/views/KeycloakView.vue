<script setup lang="ts">
import { computed, ref } from "vue";
import type { KeycloakData } from "@/types/keycloak";

import LoginView from "@/views/auth/LoginView.vue";
import RegisterView from "@/views/auth/RegisterView.vue";
import ForgotPasswordView from "@/views/auth/ForgotPasswordView.vue";
import UpdatePasswordView from "@/views/auth/UpdatePasswordView.vue";
import VerifyEmailView from "@/views/auth/VerifyEmailView.vue";
import ErrorView from "@/views/auth/ErrorView.vue";

import type { NavigationMenuItem } from "@nuxt/ui";

const kcData = ref<KeycloakData | null>();

const currentView = computed(() => {
  const page = kcData.value?.page;
  switch (page) {
    case "login":
      return LoginView;
    case "register":
      return RegisterView;
    case "forgot-password":
      return ForgotPasswordView;
    case "update-password":
      return UpdatePasswordView;
    case "verify-email":
      return VerifyEmailView;
    case "error":
      return ErrorView;
    default:
      return LoginView;
  }
});

const items = computed<NavigationMenuItem[]>(() => [
  {
    label: "Login",
    active: kcData.value?.page === "login",
    onSelect: () => {
      kcData.value = {
        page: "login",
        realm: {
          name: "helpdesk",
          displayName: "Helpdesk",
        },
        meta: {
          rememberMe: true,
          resetPasswordAllowed: true,
          registrationAllowed: true,
        },
        messages: {
          username: "Username or email",
          password: "Password",
          rememberMe: "Remember me",
          doLogin: "Sign In",
        },
        errors: {
          hasError: false,
        },
        urls: {
          loginAction:
            "http://localhost:8080/realms/helpdesk/login-actions/authenticate?session_code=-2hDT4HgvVsByNw5JuC2yyVJO-dbKvXRMfqjBPdsC38&amp;execution=cfc4045b-5b0d-499f-a0b4-2a3bbe02ceea&amp;client_id=helpdesk-client&amp;tab_id=9zmss7yK7lk&amp;client_data=eyJydSI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODAwMC9hcGkvYXV0aC9jYWxsYmFjayIsInJ0IjoiY29kZSJ9",
          forgotPassword:
            "/realms/helpdesk/login-actions/reset-credentials?client_id=helpdesk-client&amp;tab_id=9zmss7yK7lk&amp;client_data=eyJydSI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODAwMC9hcGkvYXV0aC9jYWxsYmFjayIsInJ0IjoiY29kZSJ9",
          register:
            "/realms/helpdesk/login-actions/registration?client_id=helpdesk-client&amp;tab_id=9zmss7yK7lk&amp;client_data=eyJydSI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODAwMC9hcGkvYXV0aC9jYWxsYmFjayIsInJ0IjoiY29kZSJ9",
        },
      };
    },
  },
  {
    label: "Register",
    active: kcData.value?.page === "register",
    onSelect: () => {
      kcData.value = {
        page: "register",
        realm: {
          name: "helpdesk",
          displayName: "Helpdesk",
        },
        meta: {
          registrationEmailAsUsername: false,
        },
        messages: {
          firstName: "First name",
          lastName: "Last name",
          email: "Email",
          username: "Username",
          password: "Password",
          passwordConfirm: "Confirm password",
          doRegister: "Register",
        },
        errors: {
          hasError: false,
          firstName: "",
          lastName: "",
          email: "",
          username: "",
          password: "",
          passwordConfirm: "",
        },
        urls: {
          registerAction:
            "http://localhost:8080/realms/helpdesk/login-actions/registration?session_code=B_G4RIJEZICsOXKLbnZ-jZt3J3tJRZVFrFGXjfu8W8E&amp;execution=18fd358a-0cbd-4c0a-9104-cc52d4e22b19&amp;client_id=helpdesk-client&amp;tab_id=GSCHUqYrKjk&amp;client_data=eyJydSI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODAwMC9hcGkvYXV0aC9jYWxsYmFjayIsInJ0IjoiY29kZSJ9",
          loginUrl:
            "/realms/helpdesk/login-actions/authenticate?client_id=helpdesk-client&amp;tab_id=GSCHUqYrKjk&amp;client_data=eyJydSI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODAwMC9hcGkvYXV0aC9jYWxsYmFjayIsInJ0IjoiY29kZSJ9",
        },
      };
    },
  },
  {
    label: "Forgot Password",
    active: kcData.value?.page === "forgot-password",
    onSelect: () => {
      kcData.value = {
        page: "forgot-password",
        realm: {
          name: "helpdesk",
          displayName: "Helpdesk",
        },
        meta: {},
        messages: {
          username: "Username or email",
          doSubmit: "Submit",
          backToLogin: "« Back to Login",
        },
        errors: {
          hasError: false,
          username: "",
        },
        urls: {
          resetPasswordAction:
            "http://localhost:8080/realms/helpdesk/login-actions/reset-credentials?session_code=rWCuanTnhKXYcoGLXeRvfnNCdPixWOorKlom6irZBus&amp;execution=2097d188-7d1b-4b9b-8670-946059df7343&amp;client_id=helpdesk-client&amp;tab_id=A28MaTAS_PU&amp;client_data=eyJydSI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODAwMC9hcGkvYXV0aC9jYWxsYmFjayIsInJ0IjoiY29kZSJ9",
          loginUrl:
            "/realms/helpdesk/login-actions/authenticate?client_id=helpdesk-client&amp;tab_id=A28MaTAS_PU&amp;client_data=eyJydSI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODAwMC9hcGkvYXV0aC9jYWxsYmFjayIsInJ0IjoiY29kZSJ9",
        },
      };
    },
  },
  {
    label: "Update Password",
    active: kcData.value?.page === "update-password",
    onSelect: () => {
      kcData.value = {
        page: "update-password",
        realm: {
          name: "helpdesk",
          displayName: "Helpdesk",
        },
        meta: {},
        messages: {
          passwordNew: "New Password",
          passwordConfirm: "Confirm password",
          doSubmit: "Submit",
        },
        errors: {
          hasError: false,
          password: "",
          passwordConfirm: "",
        },
        urls: {
          updatePasswordAction:
            "http://localhost:8080/realms/helpdesk/login-actions/required-action?session_code=eNn30i7TavhC-SIlwiHAXKtGYeP5pvL1tQijE4R3mx4&amp;execution=UPDATE_PASSWORD&amp;client_id=helpdesk-client&amp;tab_id=CiyvFOerZYQ&amp;client_data=eyJydSI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODAwMC9hcGkvYXV0aC9jYWxsYmFjayIsInJ0IjoiY29kZSJ9",
        },
      };
    },
  },
  {
    label: "Verify Email",
    active: kcData.value?.page === "verify-email",
    onSelect: () => {
      kcData.value = {
        page: "verify-email",
        realm: {
          name: "helpdesk",
          displayName: "Helpdesk",
        },
        meta: {},
        messages: {
          email: "rungsethyhk@gmail.com",
          doResend: "Resend email",
          backToLogin: "« Back to Login",
        },
        errors: {
          hasError: false,
        },
        urls: {
          resendEmailAction:
            "http://localhost:8080/realms/helpdesk/login-actions/required-action?session_code=6tuoL_Nk1Dt2fgcKP5shjKvOuurHf6_uS4u-xD1FGFI&amp;execution=VERIFY_EMAIL&amp;client_id=helpdesk-client&amp;tab_id=h2VGnFBfDaM&amp;client_data=eyJydSI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODAwMC9hcGkvYXV0aC9jYWxsYmFjayIsInJ0IjoiY29kZSJ9",
          loginUrl:
            "/realms/helpdesk/login-actions/authenticate?client_id=helpdesk-client&amp;tab_id=h2VGnFBfDaM&amp;client_data=eyJydSI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODAwMC9hcGkvYXV0aC9jYWxsYmFjayIsInJ0IjoiY29kZSJ9",
        },
      };
    },
  },
  {
    label: "Error",
    active: kcData.value?.page === "error",
    onSelect: () => {
      kcData.value = {
        page: "error",
        realm: {
          name: "helpdesk",
          displayName: "Helpdesk",
        },
        meta: {},
        messages: {
          backToLogin: "« Back to Login",
        },
        errors: {
          hasError: true,
          message: "Failed to send email, please try again later.",
        },
        urls: {
          loginUrl:
            "/realms/helpdesk/login-actions/authenticate?client_id=helpdesk-client&amp;tab_id=ZOq1hmcMYRA&amp;client_data=eyJydSI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODAwMC9hcGkvYXV0aC9jYWxsYmFjayIsInJ0IjoiY29kZSJ9",
        },
      };
    },
  },
]);
</script>

<template>
  <div class="min-h-screen flex items-center justify-center px-4 sm:px-6 lg:px-8 relative">
    <component v-if="kcData" :is="currentView" :kc-data="kcData" />
    <div v-else class="text-center">
      <p class="text-gray-500">Select a page from the menu to preview the Keycloak templates.</p>
    </div>

    <USlideover title="Change page">
      <UButton
        icon="i-lucide:panel-right-open"
        label="Change page"
        color="neutral"
        variant="subtle"
        class="absolute bottom-4 right-4 z-10"
      />

      <template #body>
        <UNavigationMenu
          :items="items"
          color="neutral"
          orientation="vertical"
          type="single"
          :ui="{
            link: 'h-10',
          }"
        />
      </template>
    </USlideover>
  </div>
</template>
