import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

// 路由表
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // 登录页
    {
      path: '/auth/login',
      name: 'login',
      component: () => import('@/views/auth/LoginView.vue'),
      meta: { public: true },
    },
    {
      path: '/auth/register',
      name: 'register',
      component: () => import('@/views/auth/RegisterView.vue'),
      meta: { public: true },
    },

    // 主页布局
    {
      path: '/',
      component: () => import('@/components/layout/AppLayout.vue'),
      children: [
        {
          path: '',
          redirect: '/tickets',
        },
        {
          path: '/tickets',
          name: 'ticket-list',
          component: () => import('@/views/tickets/TicketListView.vue'),
        },
        // 我创建的
        {
          path: '/tickets/my-created',
          name: 'my-created-tickets',
          component: () => import('@/views/tickets/MyCreatedTicketsView.vue'),
        },
        // 分配给我的
        {
          path: '/tickets/assigned-to-me',
          name: 'assigned-to-me-tickets',
          component: () => import('@/views/tickets/AssignedToMeTicketsView.vue'),
        },
        {
          path: '/admin/all-ticket-list',
          name: 'all-ticket-list',
          component: () => import('@/views/admin/AllTicketListView.vue'),
          meta: { roles: ['ADMIN'] },
        },
        {
          path: '/admin/sla-config',
          name: 'sla-config',
          component: () => import('@/views/admin/SLAConfigView.vue'),
          meta: { roles: ['ADMIN'] },
        },
        {
          path: '/ticket-log-controller',
          name: 'ticket-log-controller',
          component: () => import('@/views/ticket-log/TicketLogControllerView.vue'),
          meta: { roles: ['ADMIN'] },
        },
        {
          path: '/ticket-logs/:id',
          name: 'ticket-log-detail',
          component: () => import('@/views/ticket-log/TicketLogDetailView.vue'),
          meta: { roles: ['ADMIN'] },
        },
        {
          path: '/tickets/create',
          name: 'ticket-create',
          component: () => import('@/views/tickets/TicketCreateView.vue'),
        },
        {
          path: '/tickets/:id',
          name: 'ticket-detail',
          component: () => import('@/views/tickets/TicketDetailView.vue'),
        },
        {
          path: '/profile',
          name: 'profile',
          component: () => import('@/views/ProfileView.vue'),
        },
      ],
    },
  ],
})

router.beforeEach((to, _from, next) => {
  const auth = useAuthStore()
  const isPublic = to.meta.public
  if (!isPublic && !auth.token) {
    next({ name: 'login', query: { redirect: to.fullPath } })
    return
  }
  const roles = to.meta.roles as string[] | undefined
  if (roles && auth.user && !roles.includes(auth.user.role)) {
    next({ name: 'ticket-list' })
    return
  }
  next()
})

export default router
