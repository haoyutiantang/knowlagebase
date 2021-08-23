<template>
    <a-layout-footer1 style="text-align: center">
        haoyu知识库 <span v-show="user.id">欢迎 ：{{user.name}}</span>
    </a-layout-footer1>
</template>

<script lang="ts">
    import { defineComponent, computed } from 'vue';
    import store from '@/store';

    export default defineComponent({
        name: 'the-footer',
        setup() {
            const user = computed(() => store.state.user);

            return {
                user
            }
        }
    });
</script>
