package com.example.walleteam.navigation

import LoginScreen
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.walleteam.ui.authScreen.RegisterScreen
import com.example.walleteam.ui.home.HomeScreen
import com.example.walleteam.ui.payment.PaymentListScreen
import com.example.walleteam.ui.payment.RegisterPaymentScreen
import com.example.walleteam.ui.plan.CreatePlanScreen
import com.example.walleteam.ui.plan.JoinPlanScreen
import com.example.walleteam.ui.plan.PlanDetailScreen
import com.example.walleteam.ui.plan.PlanListScreen
import com.example.walleteam.ui.profileimport.ProfileScreen

import com.example.walleteam.viewmodel.auth.AuthViewModel
import com.example.walleteam.viewmodel.member.MemberViewModel
import com.example.walleteam.viewmodel.pay.PaymentViewModel
import com.example.walleteam.viewmodel.plan.*

import com.example.walleteam.viewmodel.sessionimport.SessionViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String = NavRoute.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Login
        composable(NavRoute.Login.route) {
            val authViewModel: AuthViewModel = hiltViewModel()
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(NavRoute.Home.route) {
                        popUpTo(NavRoute.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(NavRoute.Register.route)
                }
            )
        }

        // Register
        composable(NavRoute.Register.route) {
            val authViewModel: AuthViewModel = hiltViewModel()
            RegisterScreen(
                authViewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate(NavRoute.Home.route) {
                        popUpTo(NavRoute.Login.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // Home
        composable(NavRoute.Home.route) {
            val planListViewModel: PlanListViewModel = hiltViewModel()
            HomeScreen(
                planListViewModel = planListViewModel,
                onNavigateToPlanDetail = { planId ->
                    navController.navigate(NavRoute.PlanDetail.createRoute(planId))
                },
                onNavigateToCreatePlan = {
                    navController.navigate(NavRoute.CreatePlan.route)
                },
                onNavigateToJoinPlan = {
                    navController.navigate(NavRoute.JoinPlan.route)
                },
                onNavigateToProfile = {
                    navController.navigate(NavRoute.Profile.route)
                }
            )
        }

        // Plan List
        composable(NavRoute.PlanList.route) {
            val planListViewModel: PlanListViewModel = hiltViewModel()
            PlanListScreen(
                planListViewModel = planListViewModel,
                onNavigateToPlanDetail = { planId ->
                    navController.navigate(NavRoute.PlanDetail.createRoute(planId))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Create Plan
        composable(NavRoute.CreatePlan.route) {
            val createPlanViewModel: CreatePlanViewModel = hiltViewModel()
            val planListViewModel: PlanListViewModel = hiltViewModel() // Para recargar lista al volver

            CreatePlanScreen(
                createPlanViewModel = createPlanViewModel,
                onPlanCreated = {
                    navController.popBackStack()
                    planListViewModel.loadMyPlans()
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Join Plan
        composable(NavRoute.JoinPlan.route) {
            val memberViewModel: MemberViewModel = hiltViewModel()
            val planDetailViewModel: PlanDetailViewModel = hiltViewModel()

            JoinPlanScreen(
                memberViewModel = memberViewModel,
                planDetailViewModel = planDetailViewModel,
                onJoinSuccess = {
                    navController.popBackStack()
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Plan Detail
        composable(
            route = NavRoute.PlanDetail.route,
            arguments = listOf(navArgument("planId") { type = NavType.StringType })
        ) { backStackEntry ->
            val planId = backStackEntry.arguments?.getString("planId") ?: ""

            val planDetailViewModel: PlanDetailViewModel = hiltViewModel()
            val memberViewModel: MemberViewModel = hiltViewModel()

            PlanDetailScreen(
                planId = planId,
                planDetailViewModel = planDetailViewModel,
                memberViewModel = memberViewModel,
                onNavigateToPaymentList = { id ->
                    navController.navigate(NavRoute.PaymentList.createRoute(id))
                },
                onNavigateToMembers = {
                    // Ya no navegamos a otra pantalla, se muestra ahí mismo
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Register Payment
        composable(
            route = NavRoute.RegisterPayment.route,
            arguments = listOf(navArgument("planId") { type = NavType.StringType })
        ) { backStackEntry ->
            val planId = backStackEntry.arguments?.getString("planId") ?: ""

            val paymentViewModel: PaymentViewModel = hiltViewModel()
            val memberViewModel: MemberViewModel = hiltViewModel()
            val planDetailViewModel: PlanDetailViewModel = hiltViewModel()

            RegisterPaymentScreen(
                planId = planId,
                paymentViewModel = paymentViewModel,
                memberViewModel = memberViewModel,
                onPaymentRegistered = {
                    navController.popBackStack()
                    planDetailViewModel.loadPlan(planId)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Payment List
        composable(
            route = NavRoute.PaymentList.route,
            arguments = listOf(navArgument("planId") { type = NavType.StringType })
        ) { backStackEntry ->
            val planId = backStackEntry.arguments?.getString("planId") ?: ""
            val paymentViewModel: PaymentViewModel = hiltViewModel()

            PaymentListScreen(
                planId = planId,
                paymentViewModel = paymentViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Profile
        composable(NavRoute.Profile.route) {
            val sessionViewModel: SessionViewModel = hiltViewModel()

            ProfileScreen(
                sessionViewModel = sessionViewModel,
                onLogout = {
                    navController.navigate(NavRoute.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
