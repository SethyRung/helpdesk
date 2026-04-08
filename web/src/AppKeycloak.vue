<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import type { KeycloakData } from "@/types/keycloak";

import LoginView from "@/views/auth/LoginView.vue";
import RegisterView from "@/views/auth/RegisterView.vue";
import ForgotPasswordView from "@/views/auth/ForgotPasswordView.vue";
import UpdatePasswordView from "@/views/auth/UpdatePasswordView.vue";
import VerifyEmailView from "@/views/auth/VerifyEmailView.vue";
import ErrorView from "@/views/auth/ErrorView.vue";

const kcData = ref<KeycloakData | null>(null);

onMounted(() => {
  kcData.value = window.$kcData || null;

  if (!kcData.value) {
    console.error("Keycloak data not found!");
  }
});

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
</script>

<template>
  <UApp>
    <div class="min-h-screen flex items-center justify-center px-4 sm:px-6 lg:px-8">
      <component v-if="kcData" :is="currentView" :kc-data="kcData" />
      <div v-else class="text-center">
        <p class="text-gray-500">Loading...</p>
      </div>
    </div>
  </UApp>
</template>
