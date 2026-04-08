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
const UIcon = resolveComponent("UIcon");
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
  fetchTickets();
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

const statCards = computed<
  {
    label: string;
    value: string;
    icon: string;
    color: "primary" | "info" | "warning" | "success";
  }[]
>(() => {
  const total = tickets.value.length;
  const open = tickets.value.filter((t) => t.status === "OPEN").length;
  const inProgress = tickets.value.filter((t) => t.status === "IN_PROGRESS").length;
  const resolved = tickets.value.filter((t) => t.status === "RESOLVED").length;

  return [
    {
      label: "Total Tickets",
      value: String(total),
      icon: "i-lucide-ticket",
      color: "primary",
    },
    {
      label: "Open Tickets",
      value: String(open),
      icon: "i-lucide-inbox",
      color: "info",
    },
    {
      label: "In Progress",
      value: String(inProgress),
      icon: "i-lucide-timer",
      color: "warning",
    },
    {
      label: "Resolved",
      value: String(resolved),
      icon: "i-lucide-check-circle",
      color: "success",
    },
  ];
});

async function fetchTickets() {
  try {
    loading.value = true;
    const res = await ticketService.getAllTickets();
    tickets.value = res.data;
  } catch (error) {
    console.error("Failed to fetch tickets:", error);
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  fetchTickets();
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
    id: "createdBy",
    header: "Created By",
    cell: ({ row }) => {
      const user = row.original.createdBy;
      const displayName =
        user.firstName && user.lastName ? `${user.firstName} ${user.lastName}` : user.username;
      return h("div", { class: "flex items-center gap-2" }, [
        h(UIcon, { name: "i-lucide-user", class: "size-4 text-muted" }),
        h("span", { class: "text-sm" }, displayName),
      ]);
    },
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
  <div class="space-y-6 p-4">
    <div>
      <h2 class="text-lg font-semibold">Overview</h2>
      <p class="text-sm text-muted-foreground">
        Welcome to your dashboard. Here's what's happening.
      </p>
    </div>

    <div class="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
      <UCard v-for="stat in statCards" :key="stat.label">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-muted-foreground">
              {{ stat.label }}
            </p>
            <p class="text-2xl font-semibold">{{ stat.value }}</p>
          </div>
          <div
            class="flex h-10 w-10 items-center justify-center rounded-lg"
            :class="`bg-${stat.color}/10 text-${stat.color}`"
          >
            <UIcon :name="stat.icon" size="24" />
          </div>
        </div>
      </UCard>
    </div>

    <div>
      <div class="mb-4 flex items-center justify-between">
        <h2 class="text-lg font-semibold">Recent Tickets</h2>

        <div class="flex gap-2">
          <CreateTicketModal @created="onTicketCreated">
            <UButton label="Create Ticket" icon="i-lucide-plus" color="primary" />
          </CreateTicketModal>
        </div>
      </div>

      <UCard>
        <UTable :data="tickets" :columns="columns" sticky="header" :loading="loading" />
      </UCard>
    </div>

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
