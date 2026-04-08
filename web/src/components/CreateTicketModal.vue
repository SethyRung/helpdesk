<script lang="ts">
import type { Ticket } from "@/types/ticket";
interface CreateTicketModalEmits {
  created: [Ticket];
}
</script>

<script setup lang="ts">
import { ref } from "vue";
import { ticketService } from "@/services/ticket.service";
import type { CreateTicketRequest } from "@/types/ticket";
import { ApiResponseCode } from "@/types";
import { priorityOptions } from "@/constants/ticket";

const emits = defineEmits<CreateTicketModalEmits>();

const toast = useToast();

const newTicket = ref<CreateTicketRequest>({
  title: "",
  description: "",
  priority: "MEDIUM",
});

async function createTicket(cb: () => void) {
  try {
    const res = await ticketService.createTicket(newTicket.value);
    if (res.status.code !== ApiResponseCode.Success) {
      throw new Error(res.status.message);
    }
    const ticket = res.data;
    emits("created", ticket);

    toast.add({ title: "Ticket created", color: "success", icon: "i-lucide-check" });
    newTicket.value = { title: "", description: "", priority: "MEDIUM" };
    cb();
  } catch (err: any) {
    toast.add({ title: "Could not create ticket", color: "error", icon: "i-lucide-x" });
  }
}
</script>

<template>
  <UModal
    title="Create New Ticket"
    :ui="{
      footer: 'flex justify-between gap-4',
    }"
  >
    <slot></slot>
    <template #body>
      <div class="space-y-4">
        <UFormField label="Title" required>
          <UInput v-model="newTicket.title" placeholder="Enter ticket title" class="w-full" />
        </UFormField>

        <UFormField label="Description">
          <UTextarea
            v-model="newTicket.description"
            placeholder="Enter ticket description"
            :rows="4"
            class="w-full"
          />
        </UFormField>

        <UFormField label="Priority" required>
          <USelectMenu
            v-model="newTicket.priority"
            :items="priorityOptions"
            value-key="value"
            placeholder="Select priority"
            class="w-full"
          />
        </UFormField>
      </div>
    </template>

    <template #footer="{ close }">
      <UButton label="Cancel" variant="ghost" color="neutral" @click="close" />
      <UButton
        label="Create Ticket"
        color="primary"
        :disabled="!newTicket.title"
        @click="createTicket(close)"
      />
    </template>
  </UModal>
</template>
