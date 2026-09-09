package com.vineyard.omnicam.app.ui.landing

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vineyard.omnicam.app.core.theme.CyanAccent
import com.vineyard.omnicam.app.core.theme.ElectricBlue
import com.vineyard.omnicam.app.core.theme.EmeraldLive
import com.vineyard.omnicam.app.core.theme.IndigoAccent
import com.vineyard.omnicam.app.ui.share.ScanQrDialog

@Composable
fun LandingScreen(
    viewModel: LandingViewModel,
    onNavigateToDashboard: () -> Unit,
    onGuestQrScanned: ((String) -> Unit)? = null
) {
    val showByoDialog by viewModel.showByoDialog.collectAsState()
    val configuredProjectId by viewModel.configuredProjectId.collectAsState()
    val isDriveConnected by viewModel.isDriveConnected.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()

    var showScanQrDialog by remember { mutableStateOf(false) }

    // BYO-Firebase Configuration Dialog
    if (showByoDialog) {
        ByoFirebaseDialog(
            onDismiss = { viewModel.dismissByoDialog() },
            onSaveJson = { json -> viewModel.saveByoFirebase(json) }
        )
    }

    // CameraX Guest QR Scanner Dialog
    if (showScanQrDialog) {
        ScanQrDialog(
            onDismissRequest = { showScanQrDialog = false },
            onQrCodeScanned = { rawPayload ->
                showScanQrDialog = false
                if (onGuestQrScanned != null) {
                    onGuestQrScanned(rawPayload)
                }
                onNavigateToDashboard()
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Hero Icon with Cyan Halo
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(CyanAccent.copy(alpha = 0.4f), Color.Transparent)
                    )
                )
                .border(2.dp, CyanAccent, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Videocam,
                contentDescription = "OmniCam Vision Logo",
                tint = CyanAccent,
                modifier = Modifier.size(44.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "OmniCam Vision",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Universal Multi-Brand Security Dashboard",
            style = MaterialTheme.typography.titleMedium,
            color = CyanAccent
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Zero-Cost Serverless Architecture. Unify TP-Link Tapo, Reolink, Dahua, Hikvision & Tuya Smart Bulbs without monthly cloud subscriptions.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Architecture Highlights Pills
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            PillarBadge(icon = Icons.Default.FlashOn, label = "Direct P2P", color = CyanAccent)
            PillarBadge(icon = Icons.Default.CloudQueue, label = "15 GB Drive", color = EmeraldLive)
            PillarBadge(icon = Icons.Default.Security, label = "Serverless", color = IndigoAccent)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Real-Time Feedback Banner (Displays connection status or alerts)
        if (statusMessage != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EmeraldLive.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldLive, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = statusMessage!!, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                    }
                    IconButton(onClick = { viewModel.clearStatusMessage() }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Action Card 1: Quick Start (Local & Direct)
        ActionCard(
            title = "Quick Start (Local & P2P)",
            subtitle = "Zero configuration required. Scan local WiFi subnet and connect ONVIF/RTSP cameras instantly.",
            icon = Icons.Default.FlashOn,
            accentColor = CyanAccent,
            testTag = "action_quick_start",
            onClick = onNavigateToDashboard
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Action Card 2: Connect Google Account (Drive Cloud)
        val driveSubtitle = if (isDriveConnected) {
            "Connected: ${currentUser?.email ?: "Google Account Active"}. Free 15 GB Drive cloud backup ready."
        } else {
            "Enables free 15 GB Google Drive anti-theft cloud clip uploads via OAuth 2.0 PKCE. Opens secure browser and returns here."
        }
        ActionCard(
            title = if (isDriveConnected) "Google Drive Connected" else "Connect Google Account",
            subtitle = driveSubtitle,
            icon = if (isDriveConnected) Icons.Default.CloudDone else Icons.Default.CloudQueue,
            accentColor = EmeraldLive,
            statusBadge = if (isDriveConnected) "Connected" else null,
            testTag = "action_google_signin",
            onClick = {
                viewModel.connectGoogleDrive()
            }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Action Card 3: Join as Guest (Scan QR Code)
        ActionCard(
            title = "Join as Guest (Scan QR Code)",
            subtitle = "Scan an Admin's encrypted QR code to instantly connect to shared home cameras with zero password entry.",
            icon = Icons.Default.QrCodeScanner,
            accentColor = IndigoAccent,
            testTag = "action_scan_guest_qr",
            onClick = { showScanQrDialog = true }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Action Card 4: Bring-Your-Own Firebase
        val byoSubtitle = if (configuredProjectId != null) {
            "Project: $configuredProjectId is active. Generated member QR codes will automatically bundle these credentials."
        } else {
            "Privacy-first architecture. Supply your own google-services.json for dedicated private cloud storage."
        }
        ActionCard(
            title = if (configuredProjectId != null) "House Admin Firebase Configured" else "Bring-Your-Own Firebase",
            subtitle = byoSubtitle,
            icon = if (configuredProjectId != null) Icons.Default.CheckCircle else Icons.Default.Storage,
            accentColor = if (configuredProjectId != null) EmeraldLive else ElectricBlue,
            statusBadge = if (configuredProjectId != null) "Active: $configuredProjectId" else null,
            testTag = "action_byo_firebase",
            onClick = { viewModel.openByoDialog() }
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Deliberate Gate to Enter Dashboard
        Button(
            onClick = onNavigateToDashboard,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("launch_dashboard_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CyanAccent)
        ) {
            Text(
                text = "Enter Security Dashboard",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun PillarBadge(icon: ImageVector, label: String, color: Color) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = color, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = color)
    }
}

@Composable
private fun ActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    statusBadge: String? = null,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .testTag(testTag),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f))
                    .border(1.dp, accentColor.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (statusBadge != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(accentColor.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = statusBadge,
                                style = MaterialTheme.typography.labelSmall,
                                color = accentColor
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}