package com.forsythe.iris.presentation.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.forsythe.iris.data.room.MessageRecord
import java.time.format.DateTimeFormatter
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(){
    val homeViewModel = hiltViewModel<HomeScreenViewModel>()
    val listOfRecords by remember { homeViewModel.getRecords() }.collectAsState()

    Scaffold(
       topBar = {
           TopAppBar(
               title = { Text(text = "Iris") }
           )
       }
    ) {padding->
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn {
                items(listOfRecords){messageRecord->
                    Card (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp),
                       // elevation = CardDefaults.elevatedCardElevation(10.dp)


                    ){
                        Column (modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp)
                        )
                        {
                            Text(text = "transaction code: ${messageRecord.transactionCode}")
                            Text(text = "transaction type: ${messageRecord.transactionType}")
                            Text(text = "amount: ${messageRecord.amount}", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                           // Text(text = "amount: ${Date(messageRecord.timestamp).to}")
                        }
                    }


                }
            }
        }
    }
}