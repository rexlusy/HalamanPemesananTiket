package com.example.halamantiket

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.util.Locale
import kotlin.time.Duration.Companion.seconds

enum class BookingStatus {
    IDLE,
    LOADING,
    SUCCESS,
    EMPTY_NAME
}

@Composable
fun TicketBookingParent() {
    // State managed by Parent
    var namaPembeli by remember { mutableStateOf("") }
    var jumlahTiket by remember { mutableIntStateOf(1) }
    val hargaPerTiket = 50000L
    var status by remember { mutableStateOf(BookingStatus.IDLE) }
    
    // Total price state hoisted (derived state)
    val totalHarga = remember(jumlahTiket) { jumlahTiket * hargaPerTiket }

    // LaunchedEffect to handle the processing delay
    LaunchedEffect(status) {
        if (status == BookingStatus.LOADING) {
            delay(5.seconds) // 5 seconds delay as requested
            status = BookingStatus.SUCCESS
        }
    }

    TicketBookingScreen(
        nama = namaPembeli,
        onNamaChange = { namaPembeli = it },
        jumlah = jumlahTiket,
        onJumlahChange = { jumlahTiket = it },
        hargaTiket = hargaPerTiket,
        totalHarga = totalHarga,
        status = status,
    ) {
        status = if (namaPembeli.isBlank()) {
            BookingStatus.EMPTY_NAME
        } else {
            BookingStatus.LOADING
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TicketBookingPreview() {
    TicketBookingParent()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketBookingScreen(
    nama: String,
    onNamaChange: (String) -> Unit,
    jumlah: Int,
    onJumlahChange: (Int) -> Unit,
    hargaTiket: Long,
    totalHarga: Long,
    status: BookingStatus,
    onPesanClick: () -> Unit,
) {
    val localeID = Locale.forLanguageTag("id-ID")
    val currencyFormatter = NumberFormat.getCurrencyInstance(localeID)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pemesanan Tiket", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1976D2)),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Price Info
            Column {
                Text(text = "Harga per Tiket", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(
                    text = currencyFormatter.format(hargaTiket),
                    fontSize = 18.sp,
                    color = Color(0xFF1976D2),
                    fontWeight = FontWeight.Bold,
                )
            }

            // Name Input
            Column {
                Text(text = "Nama", fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = nama,
                    onValueChange = onNamaChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Masukkan nama Anda") },
                    singleLine = true,
                )
            }

            // Quantity Selector
            Column {
                Text(text = "Jumlah Tiket", fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    FilledTonalButton(
                        onClick = { if (jumlah > 1) onJumlahChange(jumlah - 1) },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(imageVector = Icons.Default.Remove, contentDescription = "Kurang")
                    }
                    
                    Text(
                        text = jumlah.toString(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 32.dp),
                    )

                    FilledTonalButton(
                        onClick = { onJumlahChange(jumlah + 1) },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah")
                    }
                }
            }

            // Total Price Display
            HorizontalDivider()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = "Total Bayar", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(
                    text = currencyFormatter.format(totalHarga),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black,
                )
            }

            // Pesan Button
            Button(
                onClick = onPesanClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (status == BookingStatus.LOADING) Color.Gray else Color(0xFF1976D2),
                ),
                enabled = status != BookingStatus.LOADING,
            ) {
                Text(text = "Pesan Tiket", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Status Card
            StatusIndicator(status = status)
        }
    }
}

@Composable
fun StatusIndicator(status: BookingStatus) {
    val backgroundColor = when (status) {
        BookingStatus.IDLE -> Color(0xFFF5F5F5)
        BookingStatus.LOADING -> Color(0xFFE3F2FD)
        BookingStatus.SUCCESS -> Color(0xFFE8F5E9)
        BookingStatus.EMPTY_NAME -> Color(0xFFFFEBEE)
    }

    val icon = when (status) {
        BookingStatus.IDLE -> Icons.Default.Info
        BookingStatus.LOADING -> null
        BookingStatus.SUCCESS -> Icons.Default.CheckCircle
        BookingStatus.EMPTY_NAME -> Icons.Default.Warning
    }

    val iconColor = when (status) {
        BookingStatus.IDLE -> Color.Gray
        BookingStatus.LOADING -> Color(0xFF1976D2)
        BookingStatus.SUCCESS -> Color(0xFF4CAF50)
        BookingStatus.EMPTY_NAME -> Color(0xFFF44336)
    }

    val message = when (status) {
        BookingStatus.IDLE -> "Status: Silakan pesan tiket"
        BookingStatus.LOADING -> "Status: Memproses pesanan........."
        BookingStatus.SUCCESS -> "Status: Tiket telah dipesan"
        BookingStatus.EMPTY_NAME -> "Status: Nama Masih Kosong"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (status == BookingStatus.LOADING) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = iconColor,
                    strokeWidth = 2.dp,
                )
            } else {
                icon?.let {
                    Icon(imageVector = it, contentDescription = null, tint = iconColor)
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = message,
                color = if (status == BookingStatus.EMPTY_NAME) Color.Red else Color.Black,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}
