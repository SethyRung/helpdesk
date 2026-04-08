<script setup lang="ts">
import type { KeycloakData } from "@/types/keycloak";

const props = defineProps<{
  kcData: KeycloakData;
}>();

const message = props.kcData.errors.message as string | undefined | null;
</script>

<template>
  <UCard class="w-full max-w-md">
    <template #header>
      <h1 class="text-xl font-semibold">An unexpected error occurred.</h1>
    </template>

    <div class="space-y-4">
      <p v-if="message" class="text-sm text-toned">
        {{ message }}
      </p>

      <p class="text-sm text-toned">
        If you continue to experience issues, please contact support.
      </p>

      <a :href="kcData.urls.loginUrl" class="block">
        <UButton label="Back to login" size="xl" block />
      </a>
    </div>
  </UCard>
</template>
