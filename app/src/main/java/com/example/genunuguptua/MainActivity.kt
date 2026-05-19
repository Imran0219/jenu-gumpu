package com.example.genunuguptua

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.genunuguptua.data.HarvestEntry
import com.example.genunuguptua.ui.HoneyViewModel
import com.example.genunuguptua.ui.theme.GenunuGuptuaTheme
import java.util.Locale
import com.example.genunuguptua.R

class MainActivity : ComponentActivity() {
    private val viewModel: HoneyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GenunuGuptuaTheme {
                JenuGumpuApp(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JenuGumpuApp(viewModel: HoneyViewModel) {
    val harvests by viewModel.allHarvests.collectAsState(initial = emptyList())
    val totalStock by viewModel.totalStock.collectAsState(initial = 0.0)
    var showAddDialog by remember { mutableStateOf(false) }

    val sereneGreen = Color(0xFF2E7D32)
    val lightSereneGreen = Color(0xFFE8F5E9)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        stringResource(R.string.app_name),
                        fontWeight = FontWeight.Bold,
                        color = sereneGreen
                    ) 
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = lightSereneGreen
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = sereneGreen,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_harvest))
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp)
        ) {
            // Collective Stock Summary
            CollectiveStockCard(totalStock ?: 0.0, lightSereneGreen, sereneGreen)

            Spacer(modifier = Modifier.height(16.dp))

            // Price Monitor Section
            PriceMonitorSection()

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.harvest_log),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(harvests) { harvest ->
                    HarvestCard(harvest)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            ProfitCalculatorSection(viewModel)
        }

        if (showAddDialog) {
            AddHarvestDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { date, loc, qty, floral, grade ->
                    viewModel.addHarvest(date, loc, qty, floral, grade, 18.0, "Golden")
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun CollectiveStockCard(total: Double, bgColor: Color, textColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.collective_stock_label),
                style = MaterialTheme.typography.labelLarge,
                color = textColor
            )
            Text(
                text = String.format(Locale.getDefault(), "%.2f kg", total),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Black,
                color = textColor
            )
        }
    }
}

@Composable
fun PriceMonitorSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.DesktopWindows, contentDescription = null, tint = Color(0xFFE65100))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(stringResource(R.string.price_monitor), fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Retail: ₹600/kg", style = MaterialTheme.typography.bodySmall)
                    Text("Wholesale: ₹350/kg", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun HarvestCard(harvest: HarvestEntry) {
    val cardColor = when (harvest.grade.uppercase()) {
        "A" -> Color(0xFFFFFDE7)
        "B" -> Color(0xFFFFF8E1)
        else -> Color(0xFFFAFAFA)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = harvest.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                // Requirement: Icons for grading
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val stars = when(harvest.grade.uppercase()) {
                        "A" -> 3
                        "B" -> 2
                        else -> 1
                    }
                    repeat(stars) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFFFFC107)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = harvest.floralSource,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = harvest.location,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = String.format(Locale.getDefault(), "%.2f kg", harvest.quantity),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )
            Text(
                text = "Batch ID: JB-${harvest.id + 1000}",
                style = MaterialTheme.typography.labelSmall,
                color = Color.LightGray
            )
        }
    }
}

@Composable
fun ProfitCalculatorSection(viewModel: HoneyViewModel) {
    var quantity by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<Double?>(null) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "💰 " + stringResource(R.string.profit_calculator),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text(stringResource(R.string.quantity_label)) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text(stringResource(R.string.price_label)) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
            Button(
                onClick = {
                    val q = quantity.toDoubleOrNull() ?: 0.0
                    val p = price.toDoubleOrNull() ?: 0.0
                    result = viewModel.calculateProfit(q, p, q * 50)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(stringResource(R.string.calculate_profit))
            }
            result?.let {
                Text(
                    text = String.format(Locale.getDefault(), stringResource(R.string.estimated_earnings), it),
                    modifier = Modifier.padding(top = 12.dp),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun AddHarvestDialog(onDismiss: () -> Unit, onAdd: (String, String, Double, String, String) -> Unit) {
    var date by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var floral by remember { mutableStateOf("") }
    var grade by remember { mutableStateOf("A") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_harvest)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text(stringResource(R.string.date_label)) })
                OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text(stringResource(R.string.location_label)) })
                OutlinedTextField(
                    value = quantity, 
                    onValueChange = { quantity = it }, 
                    label = { Text(stringResource(R.string.quantity_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(value = floral, onValueChange = { floral = it }, label = { Text(stringResource(R.string.floral_source_label)) })
                OutlinedTextField(value = grade, onValueChange = { grade = it }, label = { Text(stringResource(R.string.grade_label)) })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onAdd(date, location, quantity.toDoubleOrNull() ?: 0.0, floral, grade)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
            ) {
                Text(stringResource(R.string.save_label))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel_label)) }
        }
    )
}
