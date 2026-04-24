<script setup lang="ts">
import { ref, reactive } from "vue";
import * as z from "zod";

import type { FormSubmitEvent } from "@nuxt/ui";
import type { KeycloakData } from "@/types/keycloak";

const props = defineProps<{
  kcData: KeycloakData;
}>();

const schema = z.object({
  username: z.string("Username is required"),
});

type Schema = z.output<typeof schema>;

const state = reactive<Partial<Schema>>({
  username: undefined,
});

const loading = ref(false);

function onSubmit(event: FormSubmitEvent<Schema>) {
  loading.value = true;

  const { data } = event;

  const form = document.createElement("form");
  form.method = "POST";
  form.action = props.kcData.urls.resetPasswordAction;

  Object.entries(data).forEach(([key, value]) => {
    const input = document.createElement("input");
    input.type = "hidden";
    input.name = key;
    input.value = String(value);
    form.appendChild(input);
  });

  document.body.appendChild(form);
  form.submit();

  loading.value = false;
}
</script>

<template>
  <UCard class="w-full max-w-md">
    <template #header>
      <div class="flex items-center gap-2">
        <UIcon name="i-lucide-life-buoy" :size="20" />

        <h1 class="text-xl font-semibold">Reset Password</h1>
      </div>
      <p class="mt-2 text-toned">Enter your username or email to receive a password reset link</p>
    </template>

    <UAlert
      v-if="kcData.errors.hasError"
      icon="i-lucide:alert-triangle"
      :title="String(kcData.errors.username || 'An error occurred')"
      color="error"
      variant="soft"
      class="mb-4"
    />

    <UForm :schema="schema" :state="state" class="space-y-6" @submit="onSubmit">
      <UFormField :label="kcData.messages.username" name="username" required>
        <UInput
          v-model="state.username"
          :placeholder="kcData.messages.username"
          autocomplete="email"
          size="xl"
          class="w-full"
        />
      </UFormField>

      <UButton :label="kcData.messages.doSubmit" size="xl" type="submit" block :loading="loading" />

      <div class="text-center text-sm">
        <span class="text-gray-600 dark:text-gray-400"> Remember password? </span>
        <a
          :href="kcData.urls.loginUrl"
          class="ml-1 font-medium text-primary-600 hover:text-primary-500 dark:text-primary-400 underline"
        >
          Sign in
        </a>
      </div>
    </UForm>
  </UCard>
</template>
