<script lang="ts">
import type { TicketPriority, Ticket } from "@/types/ticket";

interface EditTicketModalProps {
  ticket: {
    id: number;
    title: string;
    description: string | null;
    priority: TicketPriority;
  };
}

interface EditTicketModalEmits {
  updated: [Ticket];
}
</script>

<script setup lang="ts">
import { ref, watch } from "vue";
import { ticketService } from "@/services/ticket.service";
import type { UpdateTicketRequest } from "@/types/ticket";
import { ApiResponseCode } from "@/types";
import { priorityOptions } from "@/constants/ticket";

const props = defineProps<EditTicketModalProps>();
const emits = defineEmits<EditTicketModalEmits>();

const toast = useToast();

const editForm = ref<UpdateTicketRequest>({
  title: "",
  description: "",
  priority: "MEDIUM",
});

const submitting = ref(false);

watch(
  () => props.ticket,
  (ticket) => {
    editForm.value = {
      title: ticket.title,
      description: ticket.description || "",
      priority: ticket.priority,
    };
  },
  { immediate: true },
);

async function updateTicket(cb: () => void) {
  if (!props.ticket.title) return;

  submitting.value = true;
  try {
    const res = await ticketService.updateTicketAsUser(props.ticket.id, editForm.value);
    if (res.status.code !== ApiResponseCode.Success) {
      throw new Error(res.status.message);
    }
    const ticket = res.data;
    emits("updated", ticket);

    toast.add({ title: "Ticket updated", color: "success", icon: "i-lucide-check" });
    cb();
  } catch (err: any) {
    toast.add({ title: "Could not update ticket", color: "error", icon: "i-lucide-x" });
  } finally {
    submitting.value = false;
  }
}
</script>

<template>
  <UModal
    title="Edit Ticket"
    :ui="{
      footer: 'flex justify-between gap-4',
    }"
  >
    <slot></slot>
    <template #body>
      <div class="space-y-4">
        <UFormField label="Title" required>
          <UInput v-model="editForm.title" placeholder="Enter ticket title" class="w-full" />
        </UFormField>

        <UFormField label="Description">
          <UTextarea
            v-model="editForm.description"
            placeholder="Enter ticket description"
            :rows="4"
            class="w-full"
          />
        </UFormField>

        <UFormField label="Priority" required>
          <USelectMenu
            v-model="editForm.priority"
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
        label="Save"
        color="primary"
        :disabled="!editForm.title"
        :loading="submitting"
        @click="updateTicket(close)"
      />
    </template>
  </UModal>
</template>
