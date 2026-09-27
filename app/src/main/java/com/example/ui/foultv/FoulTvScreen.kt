package com.example.ui.foultv

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.data.model.FoulTvGlobalConfig
import com.example.data.model.FoulVideo
import java.util.Locale
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.BrightLavender
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkPurpleContainer
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.LiveRed
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VividViolet

enum class FoulSortOption(val title: String) {
    NEWEST("الأحدث أولاً"),
    MOST_VIEWED("الأكثر مشاهدة 🔥"),
    MOST_LIKED("الأكثر إعجاباً ❤️"),
    MOST_COMMENTED("الأكثر تفاعلاً بالتعليقات 💬")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoulTvScreen(
    videos: List<FoulVideo>,
    globalConfig: FoulTvGlobalConfig,
    onPlayVideo: (FoulVideo) -> Unit,
    onOpenPublishVideoDialog: (FoulVideo?) -> Unit,
    onOpenDeveloperAuth: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearchExpanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("الكل") }
    var selectedSortOption by remember { mutableStateOf(FoulSortOption.NEWEST) }
    var isFilterSheetOpen by remember { mutableStateOf(false) }

    // Smart Filtering & Sorting Engine
    val filteredVideos = remember(videos, searchQuery, selectedCategory, selectedSortOption) {
        var list = videos

        // Category filter
        if (selectedCategory != "الكل") {
            list = list.filter { it.category.trim().equals(selectedCategory.trim(), ignoreCase = true) }
        }

        // Smart Search across Title, Description, Tags, Author, Category
        if (searchQuery.isNotBlank()) {
            val q = searchQuery.trim().lowercase()
            list = list.filter { v ->
                v.title.lowercase().contains(q) ||
                v.description.lowercase().contains(q) ||
                v.category.lowercase().contains(q) ||
                v.authorName.lowercase().contains(q) ||
                v.tags.any { it.lowercase().contains(q) }
            }
        }

        // Sorting
        when (selectedSortOption) {
            FoulSortOption.NEWEST -> list.sortedByDescending { it.createdAt }
            FoulSortOption.MOST_VIEWED -> list.sortedByDescending { it.viewsCount }
            FoulSortOption.MOST_LIKED -> list.sortedByDescending { it.likesCount }
            FoulSortOption.MOST_COMMENTED -> list.sortedByDescending { it.commentsCount }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // TOP APP BAR: FOUL TV Brand + Search Icon + Smart Filter Icon
        Surface(
            color = DarkSurface,
            border = BorderStroke(0.5.dp, DarkSurfaceBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // FOUL TV Title & Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ElectricPurple,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "FOUL TV",
                                    color = Color.White,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                )
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = LiveRed.copy(alpha = 0.2f),
                                    border = BorderStroke(0.8.dp, LiveRed)
                                ) {
                                    Text(
                                        text = "VIDEOS",
                                        color = LiveRed,
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = "أقوى اللقطات والأهداف والملخصات الحصرية",
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Top Action Icons (Search, Filter, Publish Video / Dev)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Search Button
                        Surface(
                            shape = CircleShape,
                            color = if (isSearchExpanded) ElectricPurple else DarkSurfaceVariant,
                            border = BorderStroke(1.dp, if (isSearchExpanded) BrightLavender else DarkSurfaceBorder),
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .clickable { isSearchExpanded = !isSearchExpanded }
                                .testTag("foul_tv_search_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "بحث",
                                    tint = if (isSearchExpanded) Color.White else BrightLavender,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // Smart Filter Button
                        Surface(
                            shape = CircleShape,
                            color = if (selectedSortOption != FoulSortOption.NEWEST || selectedCategory != "الكل") VividViolet else DarkSurfaceVariant,
                            border = BorderStroke(1.dp, DarkSurfaceBorder),
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .clickable { isFilterSheetOpen = true }
                                .testTag("foul_tv_filter_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.FilterList,
                                    contentDescription = "فلترة وتصفية",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                // Expandable Smart Search Bar
                AnimatedVisibility(
                    visible = isSearchExpanded,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("ابحث في عناوين الفيديوهات، الأهداف، الفرق...", fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = BrightLavender, modifier = Modifier.size(18.dp))
                            },
                            trailingIcon = {
                                if (searchQuery.isNotBlank()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(imageVector = Icons.Default.Clear, contentDescription = "مسح", tint = TextSecondary, modifier = Modifier.size(16.dp))
                                    }
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonPurple,
                                unfocusedBorderColor = DarkSurfaceBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("foul_tv_search_input")
                        )
                    }
                }
            }
        }

        // CATEGORIES HORIZONTAL PILLS BAR
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentPadding = PaddingValues(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            item {
                FoulCategoryChip(
                    title = "الكل",
                    isSelected = selectedCategory == "الكل",
                    onClick = { selectedCategory = "الكل" }
                )
            }

            items(FOUL_TV_CATEGORIES) { cat ->
                FoulCategoryChip(
                    title = cat,
                    isSelected = selectedCategory == cat,
                    onClick = { selectedCategory = cat }
                )
            }
        }

        // ACTIVE FILTER SUMMARY BADGE (If filtered or sorted)
        if (selectedSortOption != FoulSortOption.NEWEST || selectedCategory != "الكل" || searchQuery.isNotBlank()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "النتائج (${filteredVideos.size})",
                        color = BrightLavender,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = DarkSurfaceVariant,
                        border = BorderStroke(0.8.dp, DarkSurfaceBorder)
                    ) {
                        Text(
                            text = selectedSortOption.title,
                            color = TextSecondary,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = "إعادة ضبط",
                    color = LiveRed,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        searchQuery = ""
                        selectedCategory = "الكل"
                        selectedSortOption = FoulSortOption.NEWEST
                    }
                )
            }
        }

        // VIDEOS FEED (YouTube-style Cards)
        if (filteredVideos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(DarkPurpleContainer, CircleShape)
                            .border(1.2.dp, VividViolet.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Movie,
                            contentDescription = null,
                            tint = BrightLavender,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Text(
                        text = if (searchQuery.isNotBlank()) "لم يتم العثور على فيديوهات مطابقة للبحث" else "لا توجد فيديوهات في FOUL TV حالياً",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "يمكن للمطور نشر أهداف وملخصات جديدة من لوحة التحكم",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )

                    Button(
                        onClick = onOpenDeveloperAuth,
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("foul_tv_empty_dev_button")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("لوحة نشر فيديوهات FOUL TV", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("foul_tv_videos_feed"),
                contentPadding = PaddingValues(top = 4.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filteredVideos, key = { it.id }) { video ->
                    FoulVideoCard(
                        video = video,
                        globalConfig = globalConfig,
                        onClick = { onPlayVideo(video) },
                        onEdit = { onOpenPublishVideoDialog(video) }
                    )
                }
            }
        }
    }

    // SMART FILTER MODAL BOTTOM SHEET
    if (isFilterSheetOpen) {
        FoulFilterBottomSheet(
            currentSort = selectedSortOption,
            currentCategory = selectedCategory,
            onSelectSort = { selectedSortOption = it },
            onSelectCategory = { selectedCategory = it },
            onDismiss = { isFilterSheetOpen = false }
        )
    }
}

@Composable
fun FoulCategoryChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) ElectricPurple else DarkSurfaceVariant,
        border = BorderStroke(1.dp, if (isSelected) BrightLavender else DarkSurfaceBorder),
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
    ) {
        Text(
            text = title,
            color = if (isSelected) Color.White else TextSecondary,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
        )
    }
}

/**
 * YouTube-inspired Video Card with 16:9 Thumbnail, duration badge, title, author info & real-time metrics
 */
@Composable
fun FoulVideoCard(
    video: FoulVideo,
    globalConfig: FoulTvGlobalConfig,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isViewsHidden = video.isViewsHidden || globalConfig.hideViewsAll
    val isLikesDisabled = video.isLikesDisabled || globalConfig.hideLikesAll
    val isCommentsDisabled = video.isCommentsDisabled || globalConfig.disableCommentsAll

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, DarkSurfaceBorder),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clickable(onClick = onClick)
            .testTag("foul_video_card_${video.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 1. 16:9 THUMBNAIL PREVIEW
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(Color(0xFF0F0F14))
            ) {
                if (video.thumbnailUrl.isNotBlank()) {
                    SubcomposeAsyncImage(
                        model = video.thumbnailUrl,
                        contentDescription = video.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        loading = {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = NeonPurple, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                            }
                        },
                        error = {
                            FoulVideoThumbnailFallback(video = video)
                        }
                    )
                } else {
                    FoulVideoThumbnailFallback(video = video)
                }

                // HD Badge (top-left)
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Black.copy(alpha = 0.7f),
                    border = BorderStroke(0.6.dp, Color.White.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "HD",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }

                // Category pill (top-right)
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = ElectricPurple.copy(alpha = 0.85f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Text(
                        text = video.category,
                        color = Color.White,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                // Play Icon Overlay in center
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(46.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        .border(1.2.dp, VividViolet.copy(alpha = 0.8f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Duration Badge (bottom-right)
                if (video.duration.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color.Black.copy(alpha = 0.8f),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = video.duration,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // 2. VIDEO INFO ROW (Avatar + Title + Author & Views/Likes/Comments Metrics)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Author Avatar
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(
                            Brush.linearGradient(listOf(NeonPurple, ElectricPurple)),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = video.authorName.take(1).uppercase(),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    // Video Title
                    Text(
                        text = video.title,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 19.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Channel name + Time ago
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = video.authorName,
                            color = BrightLavender,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(text = "•", color = TextMuted, fontSize = 10.sp)
                        Text(
                            text = formatTimeAgoCard(video.createdAt),
                            color = TextSecondary,
                            fontSize = 10.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Metrics Badges: Views, Likes, Comments
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (!isViewsHidden) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Visibility, contentDescription = null, tint = TextMuted, modifier = Modifier.size(12.dp))
                                Text(
                                    text = formatCount(video.viewsCount) + " مشاهدة",
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        if (!isLikesDisabled) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Favorite, contentDescription = null, tint = LiveRed.copy(alpha = 0.8f), modifier = Modifier.size(12.dp))
                                Text(
                                    text = formatCount(video.likesCount),
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        if (!isCommentsDisabled) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Comment, contentDescription = null, tint = BrightLavender, modifier = Modifier.size(12.dp))
                                Text(
                                    text = formatCount(video.commentsCount),
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FoulVideoThumbnailFallback(video: FoulVideo) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF1E0938),
                        Color(0xFF0F051D),
                        Color(0xFF0A0A10)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Movie,
                contentDescription = null,
                tint = BrightLavender.copy(alpha = 0.6f),
                modifier = Modifier.size(36.dp)
            )
            Text(
                text = video.title,
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoulFilterBottomSheet(
    currentSort: FoulSortOption,
    currentCategory: String,
    onSelectSort: (FoulSortOption) -> Unit,
    onSelectCategory: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkBackground,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(imageVector = Icons.Default.FilterList, contentDescription = null, tint = BrightLavender)
                    Text(text = "فلترة وتصفية فيديوهات FOUL TV", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = "إغلاق", tint = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "ترتيب النتائج حسب:", color = BrightLavender, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))

            FoulSortOption.values().forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onSelectSort(option) }
                        .padding(vertical = 4.dp, horizontal = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = currentSort == option,
                        onClick = { onSelectSort(option) },
                        colors = RadioButtonDefaults.colors(selectedColor = ElectricPurple, unselectedColor = TextSecondary)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = option.title, color = if (currentSort == option) Color.White else TextSecondary, fontSize = 13.sp, fontWeight = if (currentSort == option) FontWeight.Bold else FontWeight.Normal)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                Text("تطبيق الفلاتر", color = Color.White, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

private fun formatCount(count: Long): String {
    return when {
        count >= 1_000_000 -> String.format(Locale.US, "%.1fM", count / 1_000_000.0)
        count >= 1_000 -> String.format(Locale.US, "%.1fK", count / 1_000.0)
        else -> "$count"
    }
}

private fun formatTimeAgoCard(timeMillis: Long): String {
    val diff = (System.currentTimeMillis() - timeMillis).coerceAtLeast(0L)
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        days > 0 -> "منذ $days يوم"
        hours > 0 -> "منذ $hours ساعة"
        minutes > 0 -> "منذ $minutes دقيقة"
        else -> "الآن"
    }
}
