package com.drivepad.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeDown
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.drivepad.app.navigation.BottomNavItem
import com.drivepad.app.ui.theme.*

@Composable
fun DriveBottomNavBar(
    selectedItem: BottomNavItem,
    onItemSelected: (BottomNavItem) -> Unit,
    volume: Float,
    onVolumeChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(DriveDimens.bottomNavHeight)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        val compact = maxWidth < 980.dp
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = if (compact) 10.dp else 22.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.weight(if (compact) 1.65f else 1.35f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                BottomNavItem.entries.forEach { item ->
                    DockItem(
                        item = item,
                        selected = item == selectedItem,
                        showLabel = !compact,
                        onClick = { onItemSelected(item) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            VerticalDivider(
                modifier = Modifier
                    .padding(horizontal = if (compact) 8.dp else 18.dp)
                    .height(42.dp),
                color = MaterialTheme.colorScheme.outline,
            )

            Row(
                modifier = Modifier.weight(if (compact) 0.75f else 0.65f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(if (compact) 8.dp else 14.dp),
            ) {
                Icon(Icons.Filled.Bluetooth, "Bluetooth", tint = ElectricBlue, modifier = Modifier.size(22.dp))
                Icon(Icons.Filled.SignalCellularAlt, "Signal", tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(22.dp))
                Icon(Icons.AutoMirrored.Filled.VolumeDown, "Volume", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                Slider(
                    value = volume.coerceIn(0f, 1f),
                    onValueChange = onVolumeChange,
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.onSurface,
                        activeTrackColor = CockpitRed,
                        inactiveTrackColor = MaterialTheme.colorScheme.outline,
                    ),
                )
                if (!compact) {
                    Icon(Icons.AutoMirrored.Filled.VolumeUp, "Volume up", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                }
            }
        }
        HorizontalDivider(
            modifier = Modifier.align(Alignment.TopCenter),
            thickness = 1.dp,
            color = CockpitRed.copy(alpha = 0.7f),
        )
    }
}

@Composable
private fun DockItem(
    item: BottomNavItem,
    selected: Boolean,
    showLabel: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tint by animateColorAsState(
        targetValue = if (selected) CockpitRed else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(180),
        label = "dockTint",
    )
    Column(
        modifier = modifier
            .fillMaxHeight()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 4.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
            contentDescription = item.label,
            tint = tint,
            modifier = Modifier.size(DriveDimens.bottomNavIconSize),
        )
        if (showLabel) {
            Spacer(Modifier.height(2.dp))
            Text(item.label, style = MaterialTheme.typography.labelSmall, color = tint)
        }
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .width(34.dp)
                .height(2.dp)
                .background(if (selected) CockpitRed else Color.Transparent),
        )
    }
}
