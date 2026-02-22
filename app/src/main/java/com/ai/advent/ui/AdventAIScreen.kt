package com.ai.advent.ui

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ai.advent.ui.theme.AdventAITheme

@Composable
fun AdventAIScreen(
    selectedVariant: Int,
    onVariantSelected: (Int) -> Unit,
    isLoadingV1: Boolean,
    isLoadingV2: Boolean,
    requestV1: String,
    requestV2: String,
    responseV1: String,
    responseV2: String
) {
    AdventAITheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Column(Modifier.padding(innerPadding)) {
                // Переключатель вариантов
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        1 to "Просто запрос",
                        2 to "Запрос с ограничениями"
                    ).forEach { (variant, label) ->
                        Button(
                            onClick = { onVariantSelected(variant) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedVariant == variant)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (selectedVariant == variant)
                                    MaterialTheme.colorScheme.onPrimary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            Text(label, textAlign = TextAlign.Center)
                        }
                    }
                }

                val isLoading = if (selectedVariant == 1) isLoadingV1 else isLoadingV2
                val request = if (selectedVariant == 1) requestV1 else requestV2
                val response = if (selectedVariant == 1) responseV1 else responseV2

                MessageScreen(
                    isLoading = isLoading,
                    request = request,
                    responseText = response
                )
            }
        }
    }
}

@Composable
private fun MessageScreen(isLoading: Boolean, request: String, responseText: String) {
    Box(Modifier.fillMaxSize()) {
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
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
            Column(
                Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(text = "Запрос: $request")
                Spacer(Modifier.height(16.dp))
                Text(text = "Ответ: $responseText")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun PreviewVariant1() {
    AdventAITheme {
        AdventAIScreen(
            selectedVariant = 1,
            onVariantSelected = {},
            isLoadingV1 = false,
            isLoadingV2 = false,
            requestV1 = "Расскажи про лучшие бесплатные нейросети",
            requestV2 = "Расскажи про лучшие бесплатные нейросети (с форматом)",
            responseV1 = "ChatGPT, Gemini, Llama...",
            responseV2 = "1. ChatGPT — ...\n...\n5. Llama — ...\nИтог: ...[КОНЕЦ]"
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewLoading() {
    AdventAITheme {
        AdventAIScreen(
            selectedVariant = 2,
            onVariantSelected = {},
            isLoadingV1 = true,
            isLoadingV2 = true,
            requestV1 = "",
            requestV2 = "",
            responseV1 = "",
            responseV2 = ""
        )
    }
}