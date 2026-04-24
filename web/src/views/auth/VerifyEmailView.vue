<script setup lang="ts">
import { ref, reactive, computed, onUnmounted } from "vue";
import * as z from "zod";

import type { FormSubmitEvent } from "@nuxt/ui";
import type { KeycloakData } from "@/types/keycloak";

const props = defineProps<{
  kcData: KeycloakData;
}>();

const RESEND_COOLDOWN = 60;

const schema = z.object({
  userEmail: z.email("Invalid email address"),
});

type Schema = z.output<typeof schema>;

const state = reactive<Partial<Schema>>({
  userEmail: props.kcData.messages.email || "",
});

const loading = ref(false);
const cooldownRemaining = ref(0);
let cooldownInterval: ReturnType<typeof setInterval> | null = null;

const canResend = computed(() => cooldownRemaining.value === 0);
const buttonText = computed(() => {
  if (loading.value) return "Sending...";
  if (cooldownRemaining.value > 0) return `Resend in ${cooldownRemaining.value}s`;
  return "Resend verification email";
});

function startCooldown() {
  cooldownRemaining.value = RESEND_COOLDOWN;
  cooldownInterval = setInterval(() => {
    cooldownRemaining.value--;
    if (cooldownRemaining.value <= 0) {
      stopCooldown();
    }
  }, 1000);
}

function stopCooldown() {
  if (cooldownInterval) {
    clearInterval(cooldownInterval);
    cooldownInterval = null;
  }
  cooldownRemaining.value = 0;
}

function onSubmit(event: FormSubmitEvent<Schema>) {
  if (!canResend.value) return;

  loading.value = true;

  const { data } = event;

  const form = document.createElement("form");
  form.method = "POST";
  form.action = props.kcData.urls.resendEmailAction;

  Object.entries(data).forEach(([key, value]) => {
    const input = document.createElement("input");
    input.type = "hidden";
    input.name = key;
    input.value = String(value);
    form.appendChild(input);
  });

  document.body.appendChild(form);
  form.submit();

  startCooldown();
  loading.value = false;
}

onUnmounted(() => {
  stopCooldown();
});
</script>

<template>
  <UCard class="w-full max-w-md">
    <template #header>
      <div class="flex items-center gap-2">
        <UIcon name="i-lucide-life-buoy" :size="20" />

        <h1 class="text-xl font-semibold">{{ kcData.realm.displayName }}</h1>
      </div>
      <p class="mt-2 text-toned">
        We've sent a verification link to <span class="font-medium">{{ state.userEmail }}</span>
      </p>
    </template>

    <UAlert
      v-if="kcData.errors.hasError"
      icon="i-lucide:alert-triangle"
      :title="String(kcData.errors.message || 'An error occurred')"
      color="error"
      variant="soft"
      class="mb-4"
    />

    <UForm :schema="schema" :state="state" class="space-y-6" @submit="onSubmit">
      <UFormField label="Email address" name="userEmail" required>
        <UInput
          v-model="state.userEmail"
          placeholder="your@email.com"
          autocomplete="email"
          size="xl"
          type="email"
          class="w-full"
          disabled
        />
      </UFormField>

      <UButton
        :label="buttonText"
        size="xl"
        type="submit"
        block
        :loading="loading"
        :disabled="!canResend"
      />

      <div class="text-center text-sm">
        <a
          :href="kcData.urls.loginUrl"
          class="font-medium text-primary-600 hover:text-primary-500 dark:text-primary-400 underline"
        >
          Back to login
        </a>
      </div>
    </UForm>
  </UCard>
</template>
