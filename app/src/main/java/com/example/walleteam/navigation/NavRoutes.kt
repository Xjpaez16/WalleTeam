package com.example.walleteam.navigation

sealed class NavRoute(val route: String) {

    object Login : NavRoute("login")
    object Register : NavRoute("register")
    object Home : NavRoute("home")
    object PlanList : NavRoute("plan_list")
    object CreatePlan : NavRoute("create_plan")
    object JoinPlan : NavRoute("join_plan")
    object PlanDetail : NavRoute("plan_detail/{planId}") {
        fun createRoute(planId: String) = "plan_detail/$planId"
    }
    object PlanMembers : NavRoute("plan_members/{planId}") {
        fun createRoute(planId: String) = "plan_members/$planId"
    }
    object RegisterPayment : NavRoute("register_payment/{planId}") {
        fun createRoute(planId: String) = "register_payment/$planId"
    }
    object PaymentList : NavRoute("payment_list/{planId}") {
        fun createRoute(planId: String) = "payment_list/$planId"
    }
    object Profile : NavRoute("profile")
}

