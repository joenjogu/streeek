package com.bizilabs.streeek.feature.issue.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bizilabs.streeek.feature.issue.IssueScreenState
import com.bizilabs.streeek.lib.common.helpers.fromHex
import com.bizilabs.streeek.lib.domain.models.LabelDomain
import com.bizilabs.streeek.lib.resources.strings.SafiStringLabels

@Composable
fun IssueScreenCreateSection(
    state: IssueScreenState,
    onValueChangeTitle: (String) -> Unit,
    onValueChangeDescription: (String) -> Unit,
    onClickOpenLabels: () -> Unit,
    onClickRemoveLabel: (LabelDomain) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.title,
            onValueChange = onValueChangeTitle,
            shape = RectangleShape,
            placeholder = {
                Text(text = stringResource(SafiStringLabels.Title))
            },
            colors =
                TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                ),
        )
        TextField(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
            value = state.description,
            onValueChange = onValueChangeDescription,
            shape = RectangleShape,
            placeholder = {
                Text(text = "Description (optional)")
            },
            colors =
                TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                ),
        )
        HorizontalDivider()
        Row(
            modifier =
                Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .fillMaxWidth()
                    .clickable(
                        onClick =
                        onClickOpenLabels,
                    ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier =
                    Modifier
                        .padding(16.dp),
                text = "Labels",
            )
            LazyRow(
                modifier = Modifier.weight(1f),
            ) {
                items(state.labels) { label ->
                    Card(
                        modifier = Modifier.padding(horizontal = 4.dp),
                        onClick = { onClickRemoveLabel(label) },
                        colors =
                            CardDefaults.cardColors(
                                containerColor = Color.fromHex(label.color),
                                contentColor = Color.Black,
                            ),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                                text = label.name,
                                style = MaterialTheme.typography.labelLarge,
                            )
                            Icon(
                                modifier =
                                    Modifier
                                        .padding(end = 4.dp)
                                        .size(12.dp),
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "remove label",
                            )
                        }
                    }
                }
            }
            IconButton(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                onClick = onClickOpenLabels,
            ) {
                Icon(
                    imageVector = if (state.isSelectingLabels) Icons.Rounded.KeyboardArrowDown else Icons.Rounded.KeyboardArrowUp,
                    contentDescription = "add label",
                )
            }
        }
    }
}
