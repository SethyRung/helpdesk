<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";

import { ticketService } from "@/services/ticket.service";
import { commentService } from "@/services/comment.service";

import type { Ticket } from "@/types/ticket";
import type { Comment } from "@/types/comment";

import { getPriorityColor, getStatusColor } from "@/utils/color";
import { formatDate, formatFullDate } from "@/utils/date";

import { useAuthStore } from "@/stores/auth";

import Editor from "@/components/editor/Editor.vue";
import EditTicketModal from "@/components/EditTicketModal.vue";
import ChangeStatusModal from "@/components/ChangeStatusModal.vue";
import DeleteCommentModal from "@/components/DeleteCommentModal.vue";
import DeleteTicketModal from "@/components/DeleteTicketModal.vue";

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

const ticket = ref<Ticket | null>(null);
const comments = ref<Comment[]>([]);
const newComment = ref("");
const editingCommentId = ref<number | null>(null);
const editingCommentContent = ref("");
const loading = ref(false);
const loadingComments = ref(false);
const submittingComment = ref(false);

const deleteCommentModalOpen = ref(false);
const commentToDeleteId = ref<number | null>(null);

const toast = useToast();

const ticketId = computed(() => Number(route.params.id));

const priorityColor = computed(() =>
  ticket.value ? getPriorityColor(ticket.value.priority) : "neutral",
);
const statusColor = computed(() =>
  ticket.value ? getStatusColor(ticket.value.status) : "neutral",
);

const canComment = computed(() => {
  if (!ticket.value || !authStore.user) return false;
  return authStore.isAdmin || ticket.value.createdBy.username === authStore.user.username;
});

const canEditTicket = computed(() => {
  if (!ticket.value || !authStore.user) return false;
  return authStore.isAdmin || ticket.value.createdBy.username === authStore.user.username;
});

const canDeleteTicket = computed(() => {
  return authStore.isAdmin;
});

const canEditStatus = computed(() => {
  return authStore.isAdmin;
});

const canEditComment = (comment: Comment) => {
  if (!authStore.user) return false;
  return authStore.isAdmin || comment.author === authStore.user.username;
};

async function loadTicket() {
  loading.value = true;
  try {
    const response = await ticketService.getTicketById(ticketId.value);
    ticket.value = response.data;
  } catch (error) {
    console.error("Failed to load ticket:", error);
    router.push({ name: "tickets" });
  } finally {
    loading.value = false;
  }
}

async function loadComments() {
  loadingComments.value = true;
  try {
    const response = await commentService.getCommentsForTicket(ticketId.value);
    comments.value = response.data;
  } catch (error) {
    console.error("Failed to load comments:", error);
  } finally {
    loadingComments.value = false;
  }
}

async function submitComment() {
  if (!newComment.value.trim()) return;

  submittingComment.value = true;
  try {
    const response = await commentService.createComment(ticketId.value, {
      content: newComment.value.trim(),
    });
    comments.value.push(response.data);
    newComment.value = "";
    toast.add({
      title: "Comment added",
      description: "Your comment has been successfully posted.",
      color: "success",
      icon: "i-lucide-check",
    });
  } catch (error) {
    console.error("Failed to create comment:", error);
    toast.add({
      title: "Error",
      description: "Failed to post comment. Please try again.",
      color: "error",
      icon: "i-lucide-alert-circle",
    });
  } finally {
    submittingComment.value = false;
  }
}

function startEditingComment(comment: Comment) {
  editingCommentId.value = comment.id;
  editingCommentContent.value = comment.content;
}

function cancelEditingComment() {
  editingCommentId.value = null;
  editingCommentContent.value = "";
}

async function saveComment(comment: Comment) {
  if (!editingCommentContent.value.trim()) return;

  try {
    const response = await commentService.updateComment(ticketId.value, comment.id, {
      content: editingCommentContent.value.trim(),
    });
    const index = comments.value.findIndex((c) => c.id === comment.id);
    if (index !== -1) {
      comments.value[index] = response.data;
    }
    cancelEditingComment();
    toast.add({
      title: "Comment updated",
      description: "Your comment has been successfully updated.",
      color: "success",
      icon: "i-lucide-circle-check",
    });
  } catch (error) {
    console.error("Failed to update comment:", error);
    toast.add({
      title: "Error",
      description: "Failed to update comment. Please try again.",
      color: "error",
      icon: "i-lucide-alert-circle",
    });
  }
}

function openDeleteCommentModal(commentId: number) {
  commentToDeleteId.value = commentId;
  deleteCommentModalOpen.value = true;
}

function onCommentDeleted(commentId: number) {
  comments.value = comments.value.filter((c) => c.id !== commentId);
}

function onTicketUpdated(updatedTicket: Ticket) {
  ticket.value = updatedTicket;
  router.back();
}

onMounted(() => {
  loadTicket();
  loadComments();
});
</script>

<template>
  <div class="space-y-4">
    <UButton
      icon="i-lucide-arrow-left"
      label="Back to tickets"
      color="neutral"
      variant="ghost"
      @click="router.back"
    />

    <div v-if="ticket" class="grid grid-cols-1 gap-6 lg:grid-cols-3">
      <div class="lg:col-span-2 space-y-6">
        <UCard>
          <template #header>
            <div class="flex items-start justify-between gap-4">
              <div class="flex items-center gap-2">
                <UIcon name="i-lucide-ticket" class="size-5 text-muted" />
                <h1 class="text-2xl font-bold">{{ ticket.title }}</h1>
              </div>
              <div class="flex items-center gap-2">
                <EditTicketModal v-if="canEditTicket" :ticket="ticket" @updated="onTicketUpdated">
                  <UButton icon="i-lucide-pencil" label="Edit" variant="soft" />
                </EditTicketModal>

                <DeleteTicketModal
                  v-if="canDeleteTicket"
                  :ticket-id="ticket.id"
                  @deleted="() => router.push({ name: 'tickets' })"
                >
                  <UButton icon="i-lucide-trash" label="Delete" color="error" variant="soft" />
                </DeleteTicketModal>
              </div>
            </div>
          </template>

          <p class="text-default whitespace-pre-wrap">{{ ticket.description }}</p>
        </UCard>

        <UCard>
          <template #header>
            <h2 class="text-lg font-semibold">Activity</h2>
          </template>

          <div class="space-y-4">
            <div v-if="loadingComments" class="space-y-4">
              <USkeleton v-for="i in 3" :key="i" class="h-20 w-full" />
            </div>

            <UEmpty
              v-else-if="comments.length === 0"
              icon="i-lucide-message-square"
              title="No comments yet"
              description="Be the first to comment on this ticket"
            />

            <div v-else class="space-y-4">
              <div
                v-for="comment in comments"
                :key="comment.id"
                class="border-l-2 border-muted pl-4"
              >
                <div class="flex items-start justify-between gap-2">
                  <div class="flex items-center gap-2">
                    <UAvatar :text="comment.author[0]" />
                    <div>
                      <p class="text-sm font-medium">{{ comment.author }}</p>
                      <p class="text-xs text-muted">{{ formatDate(comment.createdAt) }}</p>
                    </div>
                  </div>

                  <UDropdownMenu
                    v-if="canEditComment(comment)"
                    :items="[
                      [
                        {
                          label: 'Edit',
                          icon: 'i-lucide-pencil',
                          onSelect: () => startEditingComment(comment),
                        },
                      ],
                      [
                        {
                          label: 'Delete',
                          icon: 'i-lucide-trash',
                          color: 'error',
                          onSelect: () => openDeleteCommentModal(comment.id),
                        },
                      ],
                    ]"
                  >
                    <UButton
                      icon="i-lucide-more-vertical"
                      color="neutral"
                      variant="ghost"
                      size="xs"
                    />
                  </UDropdownMenu>
                </div>

                <div v-if="editingCommentId === comment.id" class="mt-2 space-y-2">
                  <Editor
                    v-model="editingCommentContent"
                    placeholder="Edit your comment..."
                    class="min-h-24"
                  />
                  <div class="flex gap-2">
                    <UButton label="Save" @click="saveComment(comment)" />
                    <UButton label="Cancel" variant="ghost" @click="cancelEditingComment" />
                  </div>
                </div>

                <div v-else class="mt-2 prose prose-sm max-w-none dark:prose-invert">
                  <Editor :model-value="comment.content" :editable="false" />
                </div>
              </div>
            </div>

            <USeparator />

            <div v-if="canComment" class="space-y-3">
              <p class="text-sm font-medium">Add a comment</p>
              <Editor
                v-model="newComment"
                placeholder="Write your comment here... (Markdown supported)"
                :disabled="submittingComment"
                class="min-h-32"
              />
              <div class="flex justify-end">
                <UButton
                  label="Send"
                  icon="i-lucide-send"
                  :loading="submittingComment"
                  :disabled="!newComment.trim()"
                  @click="submitComment"
                />
              </div>
            </div>

            <UAlert
              v-else
              icon="i-lucide-info"
              title="View only"
              description="You don't have permission to comment on this ticket"
              color="info"
              variant="subtle"
            />
          </div>
        </UCard>
      </div>

      <div class="space-y-4">
        <UCard>
          <template #header>
            <div class="flex items-center justify-between">
              <h2 class="text-lg font-semibold">Details</h2>
              <ChangeStatusModal v-if="canEditStatus" :ticket="ticket" @updated="onTicketUpdated">
                <UButton icon="i-lucide-refresh-cw" label="Change Status" variant="soft" />
              </ChangeStatusModal>
            </div>
          </template>

          <div class="space-y-4">
            <div>
              <p class="text-xs text-muted uppercase mb-1">Status</p>
              <UBadge :label="ticket.status" :color="statusColor" variant="subtle" />
            </div>

            <div>
              <p class="text-xs text-muted uppercase mb-1">Opened by</p>
              <div class="flex items-center gap-2">
                <UAvatar :text="ticket.createdBy.username[0]" size="xs" />
                <div>
                  <p class="text-sm">
                    {{
                      ticket.createdBy.firstName && ticket.createdBy.lastName
                        ? `${ticket.createdBy.firstName} ${ticket.createdBy.lastName}`
                        : ticket.createdBy.username
                    }}
                  </p>
                  <p v-if="ticket.createdBy.email" class="text-xs text-muted">
                    {{ ticket.createdBy.email }}
                  </p>
                </div>
              </div>
            </div>

            <div>
              <p class="text-xs text-muted uppercase mb-1">Priority</p>
              <UBadge :label="ticket.priority" :color="priorityColor" variant="subtle" />
            </div>

            <div>
              <p class="text-xs text-muted uppercase mb-1">Created</p>
              <p class="text-sm">{{ formatFullDate(ticket.createdAt) }}</p>
            </div>

            <div>
              <p class="text-xs text-muted uppercase mb-1">Last Updated</p>
              <p class="text-sm">{{ formatDate(ticket.updatedAt) }}</p>
            </div>
          </div>
        </UCard>
      </div>
    </div>

    <DeleteCommentModal
      v-if="commentToDeleteId"
      v-model:open="deleteCommentModalOpen"
      :ticket-id="ticketId"
      :comment-id="commentToDeleteId"
      @deleted="onCommentDeleted"
    />
  </div>
</template>
