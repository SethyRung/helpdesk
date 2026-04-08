<script setup lang="ts">
import { computed, h, onMounted, ref, resolveComponent } from "vue";
import { useRouter } from "vue-router";

import type { TableColumn } from "@nuxt/ui";
import type { Ticket } from "@/types/ticket";

import CreateTicketModal from "@/components/CreateTicketModal.vue";
import EditTicketModal from "@/components/EditTicketModal.vue";
import ChangeStatusModal from "@/components/ChangeStatusModal.vue";
import DeleteTicketModal from "@/components/DeleteTicketModal.vue";
import { ticketService } from "@/services/ticket.service";
import { getPriorityColor, getStatusColor } from "@/utils/color";
import { formatFullDate } from "@/utils/date";
import { useAuthStore } from "@/stores/auth";

const UButton = resolveComponent("UButton");
const UBadge = resolveComponent("UBadge");
const UDropdownMenu = resolveComponent("UDropdownMenu");

const router = useRouter();
const authStore = useAuthStore();
const loading = ref(true);

const editModalOpen = ref(false);
const statusModalOpen = ref(false);
const deleteModalOpen = ref(false);
const selectedTicket = ref<Ticket | null>(null);

const isAdmin = computed(() => authStore.isAdmin);

function onTicketCreated(ticket: Ticket) {
  tickets.value.unshift(ticket);
}

function onTicketUpdated(updatedTicket: Ticket) {
  const index = tickets.value.findIndex((t) => t.id === updatedTicket.id);
  if (index !== -1) {
    tickets.value[index] = updatedTicket;
  }
}

function onTicketDeleted() {
  fetchMyTickets();
}

function openEditModal(ticket: Ticket) {
  selectedTicket.value = ticket;
  editModalOpen.value = true;
}

function openStatusModal(ticket: Ticket) {
  selectedTicket.value = ticket;
  statusModalOpen.value = true;
}

function openDeleteModal(ticket: Ticket) {
  selectedTicket.value = ticket;
  deleteModalOpen.value = true;
}

const tickets = ref<Ticket[]>([]);

async function fetchMyTickets() {
  try {
    loading.value = true;
    const res = await ticketService.getMyTickets();
    tickets.value = res.data;
  } catch (error) {
    console.error("Failed to fetch tickets:", error);
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  fetchMyTickets();
});

const columns = computed<TableColumn<Ticket>[]>(() => [
  {
    accessorKey: "id",
    header: "Ticket ID",
  },
  {
    accessorKey: "title",
    header: "Title",
  },
  {
    accessorKey: "description",
    header: "Description",
    cell: ({ row }) => h("span", { class: "line-clamp-1" }, row.original.description ?? ""),
  },
  {
    accessorKey: "priority",
    header: "Priority",
    cell: ({ row }) =>
      h(
        UBadge,
        {
          label: row.original.priority,
          color: getPriorityColor(row.original.priority),
          variant: "subtle",
        },
        {},
      ),
  },
  {
    accessorKey: "status",
    header: "Status",
    cell: ({ row }) =>
      h(
        UBadge,
        {
          label: row.original.status,
          color: getStatusColor(row.original.status),
          variant: "subtle",
        },
        {},
      ),
  },
  {
    accessorKey: "createdAt",
    header: "Created At",
    cell: ({ row }) => formatFullDate(row.original.createdAt),
  },
  {
    id: "actions",
    header: "",
    cell: ({ row }) => {
      const ticket = row.original;
      const items = [
        [
          {
            label: "View",
            icon: "i-lucide-eye",
            onSelect: () => router.push({ name: "ticket-details", params: { id: ticket.id } }),
          },
          {
            label: "Edit",
            icon: "i-lucide-pencil",
            onSelect: () => openEditModal(ticket),
          },
        ],
      ];

      if (isAdmin.value) {
        items.push([
          {
            label: "Change Status",
            icon: "i-lucide-refresh-cw",
            onSelect: () => openStatusModal(ticket),
          },
          { label: "Delete", icon: "i-lucide-trash", onSelect: () => openDeleteModal(ticket) },
        ]);
      }

      return h("div", { class: "flex justify-end" }, [
        h(
          UDropdownMenu,
          {
            items,
          },
          {
            default: () =>
              h(UButton, {
                icon: "i-lucide-more-vertical",
                color: "neutral",
                variant: "ghost",
                size: "xs",
              }),
          },
        ),
      ]);
    },
  },
]);
</script>

<template>
  <div>
    <div class="mb-4 flex items-center justify-between">
      <div>
        <h2 class="text-lg font-semibold">My Tickets</h2>
        <p class="text-sm text-muted-foreground">Manage your support tickets</p>
      </div>

      <CreateTicketModal @created="onTicketCreated">
        <UButton label="Create Ticket" icon="i-lucide-plus" color="primary" />
      </CreateTicketModal>
    </div>

    <UCard>
      <UTable :data="tickets" :columns="columns" sticky="header" :loading="loading" />
    </UCard>

    <EditTicketModal
      v-if="selectedTicket"
      v-model:open="editModalOpen"
      :ticket="selectedTicket"
      @updated="onTicketUpdated"
    />

    <ChangeStatusModal
      v-if="selectedTicket"
      v-model:open="statusModalOpen"
      :ticket="selectedTicket"
      @updated="onTicketUpdated"
    />

    <DeleteTicketModal
      v-if="selectedTicket"
      v-model:open="deleteModalOpen"
      :ticket-id="selectedTicket.id"
      @deleted="onTicketDeleted"
    />
  </div>
</template>
