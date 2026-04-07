<script setup lang="ts">
import { computed, h, onMounted, ref, resolveComponent } from "vue";
import { useRouter } from "vue-router";

import type { TableColumn, TableRow } from "@nuxt/ui";
import type { Ticket } from "@/types/ticket";

import CreateTicketModal from "@/components/CreateTicketModal.vue";
import { ticketService } from "@/services/ticket.service";
import { getPriorityColor, getStatusColor } from "@/utils/color";
import { formatFullDate } from "@/utils/date";

const UBadge = resolveComponent("UBadge");

const router = useRouter();
const loading = ref(true);

function onTicketCreated(ticket: Ticket) {
  tickets.value.unshift(ticket);
}

function onSelect(_e: Event, row: TableRow<Ticket>) {
  router.push({ name: "ticket-details", params: { id: row.id } });
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
      <UTable
        :data="tickets"
        :columns="columns"
        sticky="header"
        @select="onSelect"
        :ui="{ tbody: 'cursor-pointer' }"
        :loading="loading"
      />
    </UCard>
  </div>
</template>
