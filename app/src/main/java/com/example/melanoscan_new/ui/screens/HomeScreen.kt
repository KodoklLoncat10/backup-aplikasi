package com.example.melanoscan_new.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.melanoscan_new.R
import com.example.melanoscan_new.ui.navigation.Screen
import com.example.melanoscan_new.ui.theme.MelanoScan_newTheme
import com.example.melanoscan_new.ui.theme.neumorphic

data class Tip(val title: String, val description: String)

@Composable
fun HomeScreen(navController: NavController) {
    val tips = listOf(
        Tip(stringResource(id = R.string.tip_1_title), stringResource(id = R.string.tip_1_desc)),
        Tip(stringResource(id = R.string.tip_2_title), stringResource(id = R.string.tip_2_desc)),
        Tip(stringResource(id = R.string.tip_3_title), stringResource(id = R.string.tip_3_desc))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F2F5))
            .padding(vertical = 16.dp)
    ) {
        // Header
        Image(
            painter = painterResource(id = R.drawable.logo_melanoscan),
            contentDescription = "MelanoScan Logo",
            modifier = Modifier
                .height(40.dp)
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Hero Card
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .neumorphic(cornerRadius = 20.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFF0F2F5))
                    .padding(24.dp)
            ) {
                Column {
                    Image(
                        painter = painterResource(id = R.drawable.logo_melanoscan),
                        contentDescription = "MelanoScan Logo",
                        modifier = Modifier
                            .height(180.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        stringResource(id = R.string.home_title),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        stringResource(id = R.string.home_subtitle),
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { navController.navigate(Screen.Scan.route) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .neumorphic(cornerRadius = 28.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(stringResource(id = R.string.home_start_scan), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = "Start Scan", tint = Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Skin Health Tips
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                stringResource(id = R.string.home_skin_health_tips),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(tips) { tip ->
                    TipCard(tip)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        CollaborationFooter()
    }
}

@Composable
fun TipCard(tip: Tip) {
    Box(
        modifier = Modifier
            .width(280.dp)
            .height(140.dp)
            .neumorphic(cornerRadius = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF0F2F5))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()) {
            Text(tip.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            Text(tip.description, fontSize = 14.sp, color = Color.Gray)
        }
    }
}

@Composable
private fun CollaborationFooter() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_bumi_shalawat),
            contentDescription = "Bumi Shalawat Logo",
            modifier = Modifier.height(40.dp)
        )
        Spacer(modifier = Modifier.width(32.dp))
        Image(
            painter = painterResource(id = R.drawable.logo_research),
            contentDescription = "Research Logo",
            modifier = Modifier.height(40.dp)
        )
    }
}

@Preview(showBackground = true, locale = "in")
@Composable
fun HomeScreenPreview() {
    MelanoScan_newTheme {
        HomeScreen(navController = rememberNavController())
    }
}
