<script lang="ts">
interface DeleteTicketModalProps {
  ticketId: number;
}

interface DeleteTicketModalEmits {
  deleted: [void];
}
</script>

<script setup lang="ts">
import { ref } from "vue";
import { ticketService } from "@/services/ticket.service";

const props = defineProps<DeleteTicketModalProps>();
const emits = defineEmits<DeleteTicketModalEmits>();

const toast = useToast();
const submitting = ref(false);

async function deleteTicket(cb: () => void) {
  submitting.value = true;
  try {
    await ticketService.deleteTicket(props.ticketId);
    emits("deleted");

    toast.add({
      title: "Ticket deleted",
      description: "The ticket has been successfully deleted.",
      color: "success",
      icon: "i-lucide-check",
    });
    cb();
  } catch (err: any) {
    toast.add({
      title: "Could not delete ticket",
      description:
        err?.response?.data?.status?.message || "Failed to delete ticket. Please try again.",
      color: "error",
      icon: "i-lucide-x",
    });
  } finally {
    submitting.value = false;
  }
}
</script>

<template>
  <UModal
    title="Delete Ticket"
    :ui="{
      footer: 'flex justify-between gap-4',
    }"
  >
    <slot></slot>
    <template #body>
      <p>Are you sure you want to delete this ticket?</p>
      <p class="text-sm text-muted">
        This will permanently delete this ticket and all its comments.
      </p>
    </template>

    <template #footer="{ close }">
      <UButton label="Cancel" variant="ghost" color="neutral" @click="close" />
      <UButton
        label="Delete Ticket"
        color="error"
        icon="i-lucide-trash"
        :loading="submitting"
        @click="deleteTicket(close)"
      />
    </template>
  </UModal>
</template>
