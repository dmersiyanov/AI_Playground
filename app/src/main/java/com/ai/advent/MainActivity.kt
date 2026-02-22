package com.ai.advent

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.ai.advent.api.DeepSeekApiClient
import com.ai.advent.ui.AdventAIScreen
import com.ai.advent.ui.theme.AdventAITheme
import kotlinx.coroutines.launch

// Вариант 1: базовый запрос без ограничений
private const val REQUEST_V1 = "Расскажи про лучшие бесплатные нейросети"

// Вариант 2: тот же вопрос, но с явным описанием формата в тексте запроса
private const val REQUEST_V2 = """Расскажи про лучшие бесплатные нейросети.

Формат ответа:
- Вступление: 1–2 предложения о теме.
- Список: ровно 5 нейросетей, каждая с названием и одним предложением описания.
- Завершение: итоговая рекомендация в 1 предложении, после которой поставь маркер [КОНЕЦ].

Пример структуры:
1. Название — описание.
...
5. Название — описание.
Итог: ...[КОНЕЦ]"""

private const val SYSTEM_V2 =
    "Отвечай только на русском языке. Строго следуй заданному формату. Не добавляй ничего после маркера [КОНЕЦ]."

class MainActivity : ComponentActivity() {

    private var responseV1 by mutableStateOf("...")
    private var responseV2 by mutableStateOf("...")
    private var isLoadingV1 by mutableStateOf(true)
    private var isLoadingV2 by mutableStateOf(true)
    private var selectedVariant by mutableIntStateOf(1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val client = DeepSeekApiClient(BuildConfig.DEEPSEEK_API_KEY)

        // Вариант 1 — базовый запрос
        lifecycleScope.launch {
            try {
                responseV1 = client.sendMessageAsync(REQUEST_V1)
                Log.d("DeepSeekAPI", "V1 response: $responseV1")
            } catch (e: Exception) {
                Log.e("DeepSeekAPI", "V1 Error: ${e.message}", e)
                responseV1 = "Ошибка: ${e.message}"
            } finally {
                isLoadingV1 = false
            }
        }

        // Вариант 2 — запрос с форматом, ограничением длины и stop-последовательностью
        lifecycleScope.launch {
            try {
                responseV2 = client.sendMessageAsync(
                    message = REQUEST_V2,
                    systemPrompt = SYSTEM_V2,
                    maxTokens = 400,
                    stopSequences = listOf("[КОНЕЦ]")
                )
                Log.d("DeepSeekAPI", "V2 response: $responseV2")
            } catch (e: Exception) {
                Log.e("DeepSeekAPI", "V2 Error: ${e.message}", e)
                responseV2 = "Ошибка: ${e.message}"
            } finally {
                isLoadingV2 = false
            }
        }

        setContent {
            AdventAITheme {
                AdventAIScreen(
                    selectedVariant = selectedVariant,
                    onVariantSelected = { selectedVariant = it },
                    isLoadingV1 = isLoadingV1,
                    isLoadingV2 = isLoadingV2,
                    requestV1 = REQUEST_V1,
                    requestV2 = REQUEST_V2,
                    responseV1 = responseV1,
                    responseV2 = responseV2
                )
            }
        }
    }
}