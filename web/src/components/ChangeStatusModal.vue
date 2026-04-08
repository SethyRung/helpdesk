<script lang="ts">
import type { TicketPriority, Ticket } from "@/types/ticket";

interface ChangeStatusModalProps {
  ticket: {
    id: number;
    status: TicketStatus;
  };
}

interface ChangeStatusModalEmits {
  updated: [Ticket];
}
</script>

<script setup lang="ts">
import { ref, watch } from "vue";
import { ticketService } from "@/services/ticket.service";
import type { TicketStatus } from "@/types/ticket";
import { ApiResponseCode } from "@/types";
import { statusOptions } from "@/constants/ticket";

const props = defineProps<ChangeStatusModalProps>();
const emits = defineEmits<ChangeStatusModalEmits>();

const toast = useToast();

const selectedStatus = ref<TicketStatus>("OPEN");
const submitting = ref(false);

watch(
  () => props.ticket.status,
  (status) => {
    selectedStatus.value = status;
  },
  { immediate: true },
);

async function changeStatus(cb: () => void) {
  submitting.value = true;
  try {
    const res = await ticketService.updateTicketStatus(props.ticket.id, selectedStatus.value);
    if (res.status.code !== ApiResponseCode.Success) {
      throw new Error(res.status.message);
    }
    const ticket = res.data;
    emits("updated", ticket);

    toast.add({ title: "Status updated", color: "success", icon: "i-lucide-check" });
    cb();
  } catch (err: any) {
    toast.add({ title: "Could not update status", color: "error", icon: "i-lucide-x" });
  } finally {
    submitting.value = false;
  }
}
</script>

<template>
  <UModal
    title="Change Status"
    description="Select the new status for this ticket."
    :ui="{
      footer: 'flex justify-between gap-4',
    }"
  >
    <slot></slot>
    <template #body>
      <div class="space-y-4">
        <UFormField label="Status" required>
          <USelectMenu
            v-model="selectedStatus"
            :items="statusOptions"
            value-key="value"
            placeholder="Select status"
            class="w-full"
          />
        </UFormField>
      </div>
    </template>

    <template #footer="{ close }">
      <UButton label="Cancel" variant="ghost" color="neutral" @click="close" />
      <UButton
        label="Update Status"
        color="primary"
        :loading="submitting"
        @click="changeStatus(close)"
      />
    </template>
  </UModal>
</template>
