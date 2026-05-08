package acj.soluciones.acjsignature.shared.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import acj.soluciones.acjsignature.shared.ui.theme.DeepPurple
import acj.soluciones.acjsignature.shared.ui.theme.Magenta
import acj.soluciones.acjsignature.shared.ui.theme.White

/**
 * Componente de paginación diseñado para la navegación en documentos PDF.
 * Permite avanzar, retroceder y saltar directamente a una página específica mediante un campo editable.
 *
 * @param currentPage Número de la página actual (1-indexed).
 * @param totalPages Cantidad total de páginas en el documento.
 * @param onPrevious Callback para navegar a la página anterior.
 * @param onNext Callback para navegar a la siguiente página.
 * @param onJumpToPage Callback para saltar a un número de página específico.
 * @param modifier Modificador de diseño opcional.
 * @author Ethan Matias Aliaga Aguirre
 * @date 2026-05-01
 * @version 1.0
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ACJPager(
    currentPage: Int,
    totalPages: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onJumpToPage: (Int) -> Unit,
    modifier: Modifier = Modifier
) {

    var textValue by remember(currentPage) { mutableStateOf(currentPage.toString()) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Anterior Button
        Button(
            onClick = onPrevious,
            enabled = currentPage > 1,
            colors = ButtonDefaults.buttonColors(
                containerColor = Magenta,
                contentColor = White,
                disabledContainerColor = Magenta.copy(alpha = 0.3f),
                disabledContentColor = White.copy(alpha = 0.5f)
            ),
            modifier = Modifier
                .height(48.dp)
                .weight(1f),
            shape = MaterialTheme.shapes.medium,
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.ChevronLeft,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = "ATRÁS",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }

        // Editable Page Indicator
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(90.dp)
        ) {
            Text(
                text = "PÁGINA",
                style = MaterialTheme.typography.labelSmall,
                color = DeepPurple.copy(alpha = 0.6f)
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(
                    value = textValue,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty()) {
                            textValue = ""
                        } else if (newValue.all { it.isDigit() }) {
                            val num = newValue.toIntOrNull()
                            if (num != null && num <= totalPages) {
                                textValue = newValue
                                if (num > 0) onJumpToPage(num)
                            }
                        }
                    },
                    modifier = Modifier
                        .width(50.dp)
                        .height(48.dp),
                    textStyle = TextStyle(
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = DeepPurple
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            val num = textValue.toIntOrNull()
                            if (num != null && num in 1..totalPages) {
                                onJumpToPage(num)
                            } else {
                                textValue = currentPage.toString()
                            }
                        }
                    ),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Magenta,
                        unfocusedBorderColor = DeepPurple.copy(alpha = 0.3f),
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
                
                Text(
                    text = " / $totalPages",
                    style = MaterialTheme.typography.titleMedium,
                    color = DeepPurple,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }

        // Siguiente Button
        Button(
            onClick = onNext,
            enabled = currentPage < totalPages,
            colors = ButtonDefaults.buttonColors(
                containerColor = Magenta,
                contentColor = White,
                disabledContainerColor = Magenta.copy(alpha = 0.3f),
                disabledContentColor = White.copy(alpha = 0.5f)
            ),
            modifier = Modifier
                .height(48.dp)
                .weight(1f),
            shape = MaterialTheme.shapes.medium,
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            Text(
                text = "SIGUIENTE",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
