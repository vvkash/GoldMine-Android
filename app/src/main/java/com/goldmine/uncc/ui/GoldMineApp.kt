package com.goldmine.uncc.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.goldmine.uncc.ui.navigation.Routes
import com.goldmine.uncc.ui.navigation.WebDestination
import com.goldmine.uncc.ui.screens.MainTabScreen
import com.goldmine.uncc.ui.screens.classes.ClassFormScreen
import com.goldmine.uncc.ui.screens.classes.ClassesScreen
import com.goldmine.uncc.ui.screens.discounts.DiningScreen
import com.goldmine.uncc.ui.screens.discounts.DiscountsScreen
import com.goldmine.uncc.ui.screens.maps.CampusMapScreen
import com.goldmine.uncc.ui.screens.maps.ClassesMapScreen
import com.goldmine.uncc.ui.screens.onboarding.OnboardingScreen
import com.goldmine.uncc.ui.screens.settings.PrivacyPolicyScreen
import com.goldmine.uncc.ui.screens.urec.UrecScreen
import com.goldmine.uncc.ui.screens.web.WebScreen
import com.goldmine.uncc.ui.theme.LocalGoldMineColors

/** Root navigation graph. */
@Composable
fun GoldMineApp(
    appViewModel: AppViewModel,
    state: AppState,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val extras = LocalGoldMineColors.current

    LaunchedEffect(state.hasCompletedOnboarding) {
        if (state.hasCompletedOnboarding) appViewModel.syncPushToken()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(extras.screenBackground),
    ) {
        if (!state.loaded) return@Box

        NavHost(
            navController = navController,
            startDestination = if (state.hasCompletedOnboarding) Routes.MAIN else Routes.ONBOARDING,
        ) {
            composable(Routes.ONBOARDING) {
                OnboardingScreen(
                    onContinue = { name ->
                        appViewModel.completeOnboarding(name)
                        navController.navigate(Routes.MAIN) {
                            popUpTo(Routes.ONBOARDING) { inclusive = true }
                        }
                    },
                )
            }

            composable(Routes.MAIN) {
                MainTabScreen(
                    appViewModel = appViewModel,
                    state = state,
                    onOpenWeb = { navController.navigate(Routes.web(it)) },
                    onOpenUrec = { navController.navigate(Routes.UREC) },
                    onOpenCampusMap = { navController.navigate(Routes.CAMPUS_MAP) },
                    onOpenClasses = { navController.navigate(Routes.CLASSES) },
                    onOpenDiscounts = { navController.navigate(Routes.DISCOUNTS) },
                    onOpenDining = { navController.navigate(Routes.DINING) },
                    onOpenPrivacy = { navController.navigate(Routes.PRIVACY) },
                )
            }

            composable(Routes.CAMPUS_MAP) {
                CampusMapScreen(onBack = navController::popBackStack)
            }

            composable(Routes.CLASSES) {
                ClassesScreen(
                    classes = state.classes,
                    onBack = navController::popBackStack,
                    onAddClass = { navController.navigate(Routes.ADD_CLASS) },
                    onEditClass = { navController.navigate(Routes.editClass(it.id)) },
                    onDeleteClass = appViewModel::deleteClass,
                    onOpenMap = { navController.navigate(Routes.CLASSES_MAP) },
                )
            }

            composable(Routes.CLASSES_MAP) {
                ClassesMapScreen(
                    classes = state.classes,
                    onBack = navController::popBackStack,
                )
            }

            composable(Routes.ADD_CLASS) {
                ClassFormScreen(
                    existing = null,
                    onCancel = navController::popBackStack,
                    onSave = {
                        appViewModel.addClass(it)
                        navController.popBackStack()
                    },
                )
            }

            composable(
                route = Routes.EDIT_CLASS,
                arguments = listOf(navArgument("classId") { type = NavType.StringType }),
            ) { entry ->
                val classId = entry.arguments?.getString("classId")
                val existing = state.classes.firstOrNull { it.id == classId }
                ClassFormScreen(
                    existing = existing,
                    onCancel = navController::popBackStack,
                    onSave = {
                        appViewModel.updateClass(it)
                        navController.popBackStack()
                    },
                )
            }

            composable(Routes.UREC) {
                UrecScreen(onBack = navController::popBackStack)
            }

            composable(Routes.DISCOUNTS) {
                DiscountsScreen(onBack = navController::popBackStack)
            }

            composable(Routes.DINING) {
                DiningScreen(
                    onBack = navController::popBackStack,
                    onOpenMenu = { navController.navigate(Routes.web(WebDestination.DINING)) },
                )
            }

            composable(Routes.PRIVACY) {
                PrivacyPolicyScreen(onBack = navController::popBackStack)
            }

            composable(
                route = Routes.WEB,
                arguments = listOf(navArgument("key") { type = NavType.StringType }),
            ) { entry ->
                WebScreen(
                    destination = WebDestination.fromKey(entry.arguments?.getString("key")),
                    onBack = navController::popBackStack,
                )
            }
        }
    }
}
