<script setup lang="ts">
import type { KeycloakData } from "@/types/keycloak";

const props = defineProps<{
  kcData: KeycloakData;
}>();

const errorTitle = props.kcData.errors.title || "An error occurred";
const errorDescription =
  props.kcData.errors.description || "Something went wrong. Please try again.";
</script>

<template>
  <UCard class="w-full max-w-md">
    <template #header>
      <div class="flex items-center gap-2">
        <UIcon name="i-lucide-alert-circle" :size="20" class="text-red-500" />

        <h1 class="text-xl font-semibold">{{ errorTitle }}</h1>
      </div>
      <p class="mt-2 text-toned">{{ errorDescription }}</p>
    </template>

    <div class="space-y-4">
      <p class="text-sm text-toned">
        If you continue to experience issues, please contact support or try again later.
      </p>

      <a :href="kcData.urls.loginUrl" class="block">
        <UButton label="Back to login" size="xl" block />
      </a>
    </div>
  </UCard>
</template>
