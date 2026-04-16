package org.grakovne.lissen.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import org.grakovne.lissen.R
import org.grakovne.lissen.ui.navigation.AppNavigationService
import org.grakovne.lissen.ui.screens.settings.advanced.AdvancedSettingsNavigationItemComposable
import org.grakovne.lissen.ui.screens.settings.advanced.AdvancedSettingsSimpleItemComposable
import org.grakovne.lissen.ui.screens.settings.composable.ColorSchemeSettingsComposable
import org.grakovne.lissen.ui.screens.settings.composable.GitHubLinkComposable
import org.grakovne.lissen.ui.screens.settings.composable.LibraryOrderingSettingsComposable
import org.grakovne.lissen.ui.screens.settings.composable.LicenseFooterComposable
import org.grakovne.lissen.ui.screens.settings.composable.SettingsToggleItem
import org.grakovne.lissen.viewmodel.SettingsViewModel

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SettingsScreen(
  onBack: () -> Unit,
  navController: AppNavigationService,
) {
  val viewModel: SettingsViewModel = hiltViewModel()
  val host by viewModel.host.observeAsState()
  val autoUpdateEnabled by viewModel.autoUpdateEnabled.observeAsState(true)
  val context = androidx.compose.ui.platform.LocalContext.current
  val manualUpdateState by viewModel.manualUpdateState.observeAsState(SettingsViewModel.ManualUpdateState.Idle)

  LaunchedEffect(manualUpdateState) {
    when (val state = manualUpdateState) {
      is SettingsViewModel.ManualUpdateState.Checking -> {
        android.widget.Toast
          .makeText(
            context,
            org.grakovne.lissen.R.string.settings_screen_checking_for_updates_toast,
            android.widget.Toast.LENGTH_SHORT,
          ).show()
      }

      is SettingsViewModel.ManualUpdateState.NoUpdate -> {
        android.widget.Toast
          .makeText(
            context,
            org.grakovne.lissen.R.string.settings_screen_update_not_found_toast,
            android.widget.Toast.LENGTH_SHORT,
          ).show()
        viewModel.dismissUpdateState()
      }

      is SettingsViewModel.ManualUpdateState.Error -> {
        android.widget.Toast
          .makeText(
            context,
            org.grakovne.lissen.R.string.settings_screen_update_check_failed_toast,
            android.widget.Toast.LENGTH_LONG,
          ).show()
        viewModel.dismissUpdateState()
      }

      is SettingsViewModel.ManualUpdateState.UpdateAvailable -> {
        org.grakovne.lissen.updater.UpdateNotifier
          .showUpdateNotification(context, state.version, state.url, state.fileName)
        viewModel.dismissUpdateState()
      }

      else -> {}
    }
  }

  LaunchedEffect(Unit) {
    viewModel.refreshConnectionInfo()
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = stringResource(R.string.settings_screen_title),
            style = typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            color = colorScheme.onSurface,
          )
        },
        navigationIcon = {
          IconButton(onClick = { onBack() }) {
            Icon(
              imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
              contentDescription = "Back",
              tint = colorScheme.onSurface,
            )
          }
        },
      )
    },
    modifier =
      Modifier
        .systemBarsPadding()
        .fillMaxHeight(),
    content = { innerPadding ->
      Column(
        modifier =
          Modifier
            .fillMaxSize()
            .padding(innerPadding),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Column(
          modifier =
            Modifier
              .fillMaxWidth()
              .verticalScroll(rememberScrollState()),
          horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          AdvancedSettingsNavigationItemComposable(
            title = stringResource(R.string.connection_settings_title),
            description = stringResource(R.string.connection_settings_description),
            onclick = { navController.showConnectionSettings() },
          )

          ColorSchemeSettingsComposable(viewModel)

          LibraryOrderingSettingsComposable(viewModel)

          AdvancedSettingsNavigationItemComposable(
            title = stringResource(R.string.download_settings_title),
            description = stringResource(R.string.download_settings_description),
            onclick = { navController.showCacheSettings() },
          )

          SettingsToggleItem(
            title = stringResource(R.string.settings_screen_auto_update_title),
            description = stringResource(R.string.settings_screen_auto_update_description),
            initialState = autoUpdateEnabled,
          ) { viewModel.preferAutoUpdateEnabled(it) }

          AdvancedSettingsSimpleItemComposable(
            title = stringResource(R.string.settings_screen_check_update_title),
            description = stringResource(R.string.settings_screen_check_update_description),
            onclick = {
              viewModel.checkForUpdatesManual()
            },
          )

          AdvancedSettingsNavigationItemComposable(
            title = stringResource(R.string.settings_screen_advanced_preferences_title),
            description = stringResource(R.string.settings_screen_advanced_preferences_description),
            onclick = { navController.showAdvancedSettings() },
          )

          GitHubLinkComposable()
        }

        LicenseFooterComposable()
      }
    },
  )
}
