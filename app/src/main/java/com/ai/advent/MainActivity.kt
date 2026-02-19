package com.ai.advent

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.ai.advent.api.DeepSeekApiClient
import com.ai.advent.ui.theme.AdventAITheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private var responseText by mutableStateOf("Loading...")
    private var request by mutableStateOf("Расскажи про лучшие бесплатные нейросети")
    private var isLoading by mutableStateOf(true)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val client = DeepSeekApiClient(BuildConfig.DEEPSEEK_API_KEY)

        lifecycleScope.launch {
            try {
                val response = client.sendMessageAsync(request)
                Log.d("DeepSeekAPI", response)
                responseText = response
            } catch (e: Exception) {
                Log.e("DeepSeekAPI", "Error: ${e.message}", e)
                responseText = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }

        setContent {
            AdventAITheme {
                ClaudeMessageScreen(isLoading, request, responseText)
            }
        }
    }
}

@Composable
fun ClaudeMessageScreen(isLoading: Boolean, request: String, responseText: String) {
    AdventAITheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
        ) { innerPadding ->
            Box(Modifier.padding(innerPadding)) {
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            val infiniteTransition = rememberInfiniteTransition(label = "dots")
                            val dotProgress by infiniteTransition.animateFloat(
                                initialValue = 0f,
                                targetValue = 4f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(2500, easing = LinearEasing)
                                ),
                                label = "dots"
                            )
                            val dots = ".".repeat(dotProgress.toInt() % 4)
                            Row {
                                Text("Размышляю")
                                Text(dots, modifier = Modifier.width(16.dp))
                            }
                            Spacer(Modifier.height(16.dp))
                        }
                    }
                } else {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            text = "Запрос: $request",
                            modifier = Modifier
                                .verticalScroll(rememberScrollState())
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "Ответ: $responseText",
                            modifier = Modifier
                                .verticalScroll(rememberScrollState())
                        )
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun Preview() {
    AdventAITheme {
        ClaudeMessageScreen(
            isLoading = false,
            "request",
            responseText = "response"
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ClaudeMessageScreenLoadingPreview() {
    AdventAITheme {
        ClaudeMessageScreen(
            isLoading = true,
            "request",
            responseText = "response"
        )
    }
}