package com.example.elderhelpprototypev01.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.elderhelpprototypev01.model.BillType
import com.example.elderhelpprototypev01.ui.theme.*

/**
 * SearchResultItem represents a searchable feature/service in the app.
 */
data class SearchResultItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String,
    val iconEmoji: String,
    val keywords: List<String>,
    val actionType: SearchActionType,
    val billType: BillType? = null,
    val tabIndex: Int? = null
)

enum class SearchActionType {
    OPEN_DOCTOR,
    OPEN_BILL_TYPE,
    OPEN_BILL_SELECTION,
    OPEN_PENSION_FORM,
    OPEN_EMERGENCY,
    OPEN_PROFILE,
    SWITCH_TAB
}

/**
 * GlobalSearchModal
 *
 * Senior-accessible interactive search overlay that allows seniors to search
 * and instantly navigate to any feature, bill payment, doctor booking, form,
 * emergency service, or app setting.
 */
@Composable
fun GlobalSearchModal(
    onDismiss: () -> Unit,
    onNavigateAction: (SearchResultItem) -> Unit,
    currentLanguage: String = "English (India)"
) {
    var searchQuery by remember { mutableStateOf("") }
    val isHindi = currentLanguage.contains("Hindi")

    val searchItems = remember {
        listOf(
            SearchResultItem(
                id = "doctor",
                title = "Book Doctor Appointment",
                subtitle = "Consult top clinic doctors & specialists",
                category = "Healthcare",
                iconEmoji = "👨‍⚕️",
                keywords = listOf("doctor", "appointment", "clinic", "health", "hospital", "डॉक्टर", "स्वास्थ्य"),
                actionType = SearchActionType.OPEN_DOCTOR
            ),
            SearchResultItem(
                id = "electricity",
                title = "Pay Electricity Bill",
                subtitle = "Adani, Tata Power, MSEDCL, BESCOM",
                category = "Utilities",
                iconEmoji = "⚡",
                keywords = listOf("electricity", "power", "light", "bill", "adani", "बिजली", "बिल"),
                actionType = SearchActionType.OPEN_BILL_TYPE,
                billType = BillType.ELECTRICITY
            ),
            SearchResultItem(
                id = "water",
                title = "Pay Water Bill",
                subtitle = "Municipal Corporation & Water Board",
                category = "Utilities",
                iconEmoji = "💧",
                keywords = listOf("water", "municipal", "jal", "bill", "पानी", "जल"),
                actionType = SearchActionType.OPEN_BILL_TYPE,
                billType = BillType.WATER
            ),
            SearchResultItem(
                id = "gas",
                title = "Pay Gas Bill / Book Cylinder",
                subtitle = "Mahanagar Gas, Indane, HP, Bharat Gas",
                category = "Utilities",
                iconEmoji = "🔥",
                keywords = listOf("gas", "lpg", "cylinder", "mahanagar", "indane", "गैस", "सिलेंडर"),
                actionType = SearchActionType.OPEN_BILL_TYPE,
                billType = BillType.GAS
            ),
            SearchResultItem(
                id = "mobile",
                title = "Mobile & DTH Recharge",
                subtitle = "Jio, Airtel, Vi, BSNL prepaid & postpaid",
                category = "Utilities",
                iconEmoji = "📱",
                keywords = listOf("mobile", "recharge", "jio", "airtel", "dth", "फोन", "रिचार्ज"),
                actionType = SearchActionType.OPEN_BILL_TYPE,
                billType = BillType.MOBILE
            ),
            SearchResultItem(
                id = "pension",
                title = "Government Pension Form",
                subtitle = "Apply for Senior Pension & Life Certificate",
                category = "Govt Forms",
                iconEmoji = "📝",
                keywords = listOf("pension", "government", "form", "life certificate", "पेंशन", "फॉर्म"),
                actionType = SearchActionType.OPEN_PENSION_FORM
            ),
            SearchResultItem(
                id = "emergency",
                title = "Emergency Services (112 / 101)",
                subtitle = "Instant dialer for Police, Fire & Emergency Contact",
                category = "Emergency",
                iconEmoji = "🚨",
                keywords = listOf("emergency", "police", "fire", "sos", "call", "helpline", "आपातकाल", "पुलिस"),
                actionType = SearchActionType.OPEN_EMERGENCY
            ),
            SearchResultItem(
                id = "profile",
                title = "Edit Personal Profile",
                subtitle = "Update emergency contact number & personal details",
                category = "Account",
                iconEmoji = "👤",
                keywords = listOf("profile", "account", "contact", "name", "phone", "प्रोफाइल", "संपर्क"),
                actionType = SearchActionType.OPEN_PROFILE
            ),
            SearchResultItem(
                id = "transactions",
                title = "Transaction History",
                subtitle = "View past bill receipts & payment history",
                category = "History",
                iconEmoji = "🧾",
                keywords = listOf("transaction", "history", "receipt", "paid", "लेनदेन", "इतिहास"),
                actionType = SearchActionType.SWITCH_TAB,
                tabIndex = 2
            ),
            SearchResultItem(
                id = "voice",
                title = "Sahaay Voice Assistant",
                subtitle = "Speak to Sahaay AI for voice commands",
                category = "Assistant",
                iconEmoji = "🎙️",
                keywords = listOf("voice", "speak", "assistant", "ai", "sahaay", "आवाज़", "सहायक"),
                actionType = SearchActionType.SWITCH_TAB,
                tabIndex = 1
            ),
            SearchResultItem(
                id = "settings",
                title = "Language & App Settings",
                subtitle = "Switch between English, Hindi, Marathi, Tamil, etc.",
                category = "Settings",
                iconEmoji = "⚙️",
                keywords = listOf("settings", "language", "hindi", "english", "सेटिंग्स", "भाषा"),
                actionType = SearchActionType.SWITCH_TAB,
                tabIndex = 3
            )
        )
    }

    val filteredResults = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            searchItems
        } else {
            val q = searchQuery.trim().lowercase()
            searchItems.filter { item ->
                item.title.lowercase().contains(q) ||
                item.subtitle.lowercase().contains(q) ||
                item.category.lowercase().contains(q) ||
                item.keywords.any { it.lowercase().contains(q) }
            }
        }
    }

    val quickFilters = listOf("All", "Utilities", "Healthcare", "Emergency", "Govt Forms")
    var selectedCategoryFilter by remember { mutableStateOf("All") }

    val displayedResults = remember(filteredResults, selectedCategoryFilter) {
        if (selectedCategoryFilter == "All") {
            filteredResults
        } else {
            filteredResults.filter { it.category.equals(selectedCategoryFilter, ignoreCase = true) }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 36.dp),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = AppleCanvasBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header with Search Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isHindi) "खोजें सहायता और सेवाएं" else "Search Services & Features",
                        style = Typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = AppleTextPrimary,
                            fontSize = 20.sp
                        )
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AppleBorderSubtle.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = AppleTextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Search Input Field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = if (isHindi) "डॉक्टर, बिल, पेंशन, आपातकाल खोजें..." else "Search doctor, electricity, gas, pension...",
                            style = Typography.bodyMedium.copy(color = AppleTextMuted, fontSize = 14.sp)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Icon",
                            tint = AppleBlue,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = AppleTextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = AppleBlue,
                        unfocusedBorderColor = AppleBorderSubtle
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Category Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(quickFilters) { filter ->
                        val isSelected = selectedCategoryFilter == filter
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategoryFilter = filter },
                            label = { Text(filter, fontSize = 13.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AppleBlue,
                                selectedLabelColor = Color.White,
                                containerColor = Color.White,
                                labelColor = AppleTextPrimary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = AppleBorderSubtle,
                                selectedBorderColor = AppleBlue
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Results Counter
                Text(
                    text = if (searchQuery.isBlank()) "Popular Services (${displayedResults.size})" else "Search Results (${displayedResults.size})",
                    style = Typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = AppleTextMuted,
                        fontSize = 13.sp
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Results List
                if (displayedResults.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🔍", fontSize = 40.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No matching services found",
                                style = Typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = AppleTextPrimary)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Try searching for 'Doctor', 'Bills', or 'Emergency'",
                                style = Typography.bodySmall.copy(color = AppleTextMuted)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(displayedResults) { item ->
                            SearchResultCard(
                                item = item,
                                onClick = {
                                    onNavigateAction(item)
                                    onDismiss()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Individual result card with senior-friendly touch target
 */
@Composable
private fun SearchResultCard(
    item: SearchResultItem,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "searchCardScale"
    )

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, AppleBorderSubtle),
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Badge
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(AppleBlue.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Text(item.iconEmoji, fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Text Info
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.title,
                        style = Typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = AppleTextPrimary,
                            fontSize = 15.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.subtitle,
                    style = Typography.bodySmall.copy(
                        color = AppleTextMuted,
                        fontSize = 12.sp
                    ),
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Open Action Pill
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = AppleBlue.copy(alpha = 0.12f)
            ) {
                Text(
                    text = "Open",
                    style = Typography.labelSmall.copy(
                        color = AppleBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    ),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}
