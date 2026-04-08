import { computed, ref } from "vue";
import { defineStore } from "pinia";

import { api } from "@/utils/api";

import { ApiResponseCode, Role, type AuthUser } from "@/types";

export const useAuthStore = defineStore("auth", () => {
  function login() {
    window.location.href = `${import.meta.env.VITE_API_BASE_URL}/auth/login`;
  }

  const authenticated = ref(false);
  const user = ref<AuthUser | null>(null);
  const isAdmin = computed(() => {
    return !!user.value && user.value.roles.includes(Role.Admin);
  });

  async function checkAuthenticated() {
    try {
      const res = await api.get<boolean>("/auth/check-authenticated");

      if (res.status.code === ApiResponseCode.Unauthorized) {
        authenticated.value = false;
        return false;
      }

      if (res.status.code !== ApiResponseCode.Success) throw new Error(res.status.message);

      authenticated.value = res.data === true;
      return authenticated.value;
    } catch {
      authenticated.value = false;
      return false;
    }
  }

  async function initialize() {
    try {
      await checkAuthenticated();

      if (!authenticated.value) {
        login();
        return;
      }

      const res = await api.get<AuthUser>("/auth/me");

      if (res.status.code !== ApiResponseCode.Success) throw new Error(res.status.message);

      user.value = res.data;
    } catch {
      authenticated.value = false;
      user.value = null;
    }
  }

  async function logout() {
    try {
      const res = await api.post<AuthUser>("/auth/logout");

      if (res.status.code !== ApiResponseCode.Success) throw new Error(res.status.message);

      user.value = null;
      login();
    } catch {}
  }

  return {
    isAuthenticated: authenticated,
    user,
    isAdmin,
    login,
    logout,
    initialize,
    checkAuthenticated,
  };
});
