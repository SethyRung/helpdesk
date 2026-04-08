<script lang="ts">
interface DeleteCommentModalProps {
  ticketId: number;
  commentId: number;
}

interface DeleteCommentModalEmits {
  deleted: [number];
}
</script>

<script setup lang="ts">
import { ref } from "vue";
import { commentService } from "@/services/comment.service";

const props = defineProps<DeleteCommentModalProps>();
const emits = defineEmits<DeleteCommentModalEmits>();

const toast = useToast();
const submitting = ref(false);

async function deleteComment(cb: () => void) {
  submitting.value = true;
  try {
    await commentService.deleteComment(props.ticketId, props.commentId);
    emits("deleted", props.commentId);

    toast.add({
      title: "Comment deleted",
      description: "The comment has been successfully deleted.",
      color: "success",
      icon: "i-lucide-check",
    });
    cb();
  } catch (err: any) {
    toast.add({
      title: "Could not delete comment",
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
    title="Delete Comment"
    :ui="{
      footer: 'flex justify-between gap-4',
    }"
  >
    <slot></slot>
    <template #body>
      <p>Are you sure you want to delete this comment?</p>
      <p class="text-sm text-muted">This will permanently delete this comment.</p>
    </template>

    <template #footer="{ close }">
      <UButton label="Cancel" variant="ghost" color="neutral" @click="close" />
      <UButton
        label="Delete"
        color="error"
        icon="i-lucide-trash"
        :loading="submitting"
        @click="deleteComment(close)"
      />
    </template>
  </UModal>
</template>
