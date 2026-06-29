package com.soundscope.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.activity.compose.BackHandler
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.soundscope.app.data.UserPreferencesRepository
import com.soundscope.app.ui.ai.AIScreen
import com.soundscope.app.ui.history.HistoryScreen
import com.soundscope.app.ui.meter.MainScreen
import com.soundscope.app.ui.settings.SettingsScreen
import com.soundscope.app.ui.theme.SoundScopeTheme
import com.soundscope.app.util.LocaleManager
import com.soundscope.app.viewmodel.AIViewModel
import com.soundscope.app.viewmodel.DecibelViewModel
import com.soundscope.app.viewmodel.SettingsViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    // ✅ 使用 mutableStateOf 让权限状态变化可被 Compose 观察。
    // 否则权限回调更新后 UI 不会重组，hasPermission 永远是初始值 false，
    // 导致点击「开始测量」一直触发请求权限而非真正开始测量。
    private var hasAudioPermission by mutableStateOf(false)

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasAudioPermission = granted
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // v1.3.0 在 super.onCreate 之前应用语言设置
        // 使用 AppCompatDelegate.setApplicationLocales，会自动触发 recreate
        lifecycleScope.launch {
            val repo = UserPreferencesRepository(application)
            val prefs = repo.userPreferences.first()
            LocaleManager.applyLanguage(prefs.languageMode)
        }

        super.onCreate(savedInstanceState)

        hasAudioPermission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        setContent {
            // v1.2.0 读取主题模式（响应式切换）
            val settingsViewModel: SettingsViewModel = viewModel()
            val prefs by settingsViewModel.userPreferences.collectAsState()

            // v1.4.0 RTL 支持：阿拉伯语等语言需要从右到左布局
            val isRtl = LocaleManager.isRtl(this)
            val layoutDirection = if (isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                SoundScopeTheme(themeMode = prefs.themeMode) {
                    SoundScopeApp(
                        hasPermission = hasAudioPermission,
                        onRequestPermission = {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        },
                        onExitApp = {
                            finishAffinity()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SoundScopeApp(
    hasPermission: Boolean,
    onRequestPermission: () -> Unit,
    onExitApp: () -> Unit
) {
    val decibelViewModel: DecibelViewModel = viewModel()
    val aiViewModel: AIViewModel = viewModel()
    val settingsViewModel: SettingsViewModel = viewModel()
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    // 返回键退出确认对话框
    var showExitDialog by remember { mutableStateOf(false) }

    // 真正执行退出：先停测量再 finish
    val doExit = {
        try { decibelViewModel.stopMeasurement() } catch (_: Exception) {}
        onExitApp()
    }

    // 在分贝仪主页（Tab 0）按返回键时弹出退出确认；其它 Tab 则先回到主页
    BackHandler(enabled = true) {
        if (selectedTab == 0) {
            showExitDialog = true
        } else {
            selectedTab = 0
        }
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text(stringResource(R.string.dialog_exit_title)) },
            text = { Text(stringResource(R.string.dialog_exit_message)) },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    doExit()
                }) {
                    Text(stringResource(R.string.dialog_exit), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text(stringResource(R.string.dialog_cancel))
                }
            }
        )
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Speed, contentDescription = null) },
                    label = { Text(stringResource(R.string.tab_meter)) }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.History, contentDescription = null) },
                    label = { Text(stringResource(R.string.tab_history)) }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null) },
                    label = { Text(stringResource(R.string.tab_ai)) }
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text(stringResource(R.string.tab_settings)) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> MainScreen(
                    viewModel = decibelViewModel,
                    hasPermission = hasPermission,
                    onRequestPermission = onRequestPermission,
                    onExit = { showExitDialog = true }   // v1.2.0 主页退出按钮
                )
                1 -> HistoryScreen(viewModel = decibelViewModel)
                2 -> AIScreen(
                    aiViewModel = aiViewModel,
                    decibelViewModel = decibelViewModel,
                    onNavigateToSettings = { selectedTab = 3 }
                )
                3 -> SettingsScreen(
                    viewModel = settingsViewModel,
                    onExit = { showExitDialog = true }
                )
            }
        }
    }
}
