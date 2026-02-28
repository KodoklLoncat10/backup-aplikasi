package com.example.melanoscan_new.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.melanoscan_new.R
import com.example.melanoscan_new.ui.theme.MelanoScan_newTheme
import com.example.melanoscan_new.ui.theme.neumorphic

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutUsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.nav_about)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF0F2F5),
                    titleContentColor = Color.Black
                )
            )
        },
        containerColor = Color(0xFFF0F2F5)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Section
            HeaderSection()
            Spacer(modifier = Modifier.height(32.dp))

            // Core Mission
            CoreMissionSection()
            Spacer(modifier = Modifier.height(32.dp))
            
            // Developer Team
            DeveloperTeamSection()
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(48.dp))

            // Collaboration Footer
            CollaborationFooter()
        }
    }
}

@Composable
private fun HeaderSection() {
    Box(
        modifier = Modifier
            .size(120.dp)
            .neumorphic(cornerRadius = 60.dp)
            .clip(RoundedCornerShape(60.dp))
            .background(Color(0xFFF0F2F5)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_melanoscan),
            contentDescription = "MelanoScan Logo",
            modifier = Modifier.size(80.dp)
        )
    }
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = "MelanoScan",
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun CoreMissionSection() {
    Text(
        text = stringResource(id = R.string.about_subtitle),
        fontSize = 16.sp,
        textAlign = TextAlign.Center,
        color = Color.Gray
    )
}

@Composable
private fun DeveloperTeamSection() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            stringResource(id = R.string.about_dev_team),
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(24.dp))
        TeamMember(role = stringResource(id = R.string.about_lead_dev), name = "Izzam Falih")
        Spacer(modifier = Modifier.height(16.dp))
        TeamMember(role = stringResource(id = R.string.about_supervisor), name = "Bambang Pilu")
    }
}

@Composable
private fun TeamMember(role: String, name: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(role, fontSize = 14.sp, color = Color.Gray)
        Text(name, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Color.Black)
    }
}

@Composable
private fun CollaborationFooter() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(id = R.string.about_collaboration),
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_bumi_shalawat),
                contentDescription = "Bumi Shalawat Logo",
                modifier = Modifier.height(60.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.logo_research),
                contentDescription = "Research Logo",
                modifier = Modifier.height(60.dp)
            )
        }
    }
}

@Preview(showBackground = true, locale = "in")
@Composable
fun AboutUsScreenPreview() {
    MelanoScan_newTheme {
        AboutUsScreen(navController = rememberNavController())
    }
}
