package acj.soluciones.acjsignature.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import acj.soluciones.acjsignature.presentation.certificados.CertificadosScreen
import acj.soluciones.acjsignature.presentation.configuracion.ConfiguracionScreen
import acj.soluciones.acjsignature.presentation.firma.FirmaScreen
import acj.soluciones.acjsignature.presentation.firma.PosicionarFirmaScreen
import acj.soluciones.acjsignature.presentation.home.HomeScreen
import acj.soluciones.acjsignature.presentation.validacion.ValidacionScreen
import acj.soluciones.acjsignature.shared.ui.components.ACJBottomNavBar

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Routes that show the bottom nav bar
    val showBottomBar = currentRoute in Screen.bottomNavItems.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                ACJBottomNavBar(
                    currentRoute = currentRoute,
                    onItemSelected = { screen ->
                        navController.navigate(screen.route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToFirmar = { navController.navigate(Screen.SubirPdf.route) },
                    onNavigateToCertificados = { navController.navigate(Screen.Certificados.route) },
                    onNavigateToDocumentos = {
                        navController.navigate(Screen.DocumentosFirmados.route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToConfiguracion = {
                        navController.navigate(Screen.Configuracion.route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }

            composable(Screen.SubirPdf.route) {
                FirmaScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToPosicionar = { docId ->
                        navController.navigate(Screen.PosicionarFirma.createRoute(docId))
                    },
                )
            }

            composable(
                route = Screen.PosicionarFirma.route,
                arguments = listOf(navArgument("docId") { type = NavType.LongType }),
            ) { backStackEntry ->
                val docId = backStackEntry.arguments?.getLong("docId") ?: 0L
                PosicionarFirmaScreen(
                    docId = docId,
                    onBack = { navController.popBackStack() },
                    onConfirm = {
                        navController.navigate(Screen.DocumentosFirmados.route) {
                            popUpTo(Screen.Home.route) { inclusive = false }
                        }
                    },
                )
            }

            composable(Screen.DocumentosFirmados.route) {
                ValidacionScreen(
                    onNavigateToFirmar = { navController.navigate(Screen.SubirPdf.route) },
                )
            }

            composable(Screen.Certificados.route) {
                CertificadosScreen()
            }

            composable(Screen.Configuracion.route) {
                ConfiguracionScreen()
            }
        }
    }
}