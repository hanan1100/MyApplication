package cbi.androidapp.myapplication

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import cbi.androidapp.myapplication.ui.theme.MyApplicationTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import cbi.androidapp.myapplication.room.ItemEntity
import cbi.androidapp.myapplication.viewmodel.ItemViewModel


class MainActivity : ComponentActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
        val email = sharedPreferences.getString("EMAIL", null)
        val token = sharedPreferences.getString("TOKEN", null)

        if (email == null || token == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(email, token, Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun MainScreen(email: String, token: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val viewModel: ItemViewModel = viewModel()
    val itemList by viewModel.allItems.collectAsState()

    var itemName by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }
    var isUpdating by remember { mutableStateOf(false) }
    var currentItemId by remember { mutableStateOf<Int?>(null) }

    Column(modifier = modifier.padding(16.dp)) {
        Text(text = "Hello, $email", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.refreshData(token) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Refresh Data")
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(itemList, key = { it.id }) { item ->
                ItemCard(
                    item = item,
                    onEdit = {
                        itemName = it.itemName
                        stock = it.stock
                        unit = it.unit
                        currentItemId = it.id
                        isUpdating = true
                    },
                    onDelete = {
                        viewModel.delete(it)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = itemName, onValueChange = { itemName = it }, label = { Text("Nama Barang") })
        OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Stok") })
        OutlinedTextField(value = unit, onValueChange = { unit = it }, label = { Text("Satuan") })

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (itemName.isNotBlank() && stock.isNotBlank() && unit.isNotBlank()) {
                    val newItem = ItemEntity(id = currentItemId ?: 0, itemName = itemName, stock = stock, unit = unit)
                    if (isUpdating) {
                        viewModel.updateItem(newItem)
                    } else {
                        viewModel.insert(newItem)
                    }
                    itemName = ""
                    stock = ""
                    unit = ""
                    currentItemId = null
                    isUpdating = false
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isUpdating) "Update Barang" else "Tambah Barang")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                val sharedPreferences = context.getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE)
                sharedPreferences.edit { clear() }
                val intent = Intent(context, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout")
        }
    }
}



@Composable
fun ItemCard(item: ItemEntity, onEdit: (ItemEntity) -> Unit, onDelete: (ItemEntity) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = item.itemName, style = MaterialTheme.typography.titleMedium)
            Text(text = "Stock: ${item.stock} ${item.unit}", style = MaterialTheme.typography.bodyMedium)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                TextButton(onClick = { onEdit(item) }) {
                    Text("Edit")
                }
                TextButton(onClick = { onDelete(item) }) {
                    Text("Hapus")
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MyApplicationTheme {
        MainScreen("PreviewUser", "dummy_token")
    }
}