package me.anasmusa.portfolio.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.anasmusa.portfolio.Strings
import me.anasmusa.portfolio.api.model.SkillResponse
import me.anasmusa.portfolio.component.Chip
import me.anasmusa.portfolio.component.Title
import me.anasmusa.portfolio.core.deviceValue
import me.anasmusa.portfolio.core.stringResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import portfolio.composeapp.generated.resources.Res
import portfolio.composeapp.generated.resources.ic_hash

@OptIn(ExperimentalResourceApi::class)
@Composable
fun SkillsSection(
    modifier: Modifier,
    data: SkillResponse?,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = deviceValue(150, 220).dp)
    ) {

        data?.let { data ->
            Title(
                Res.drawable.ic_hash,
                Strings.skills
            )

            SkillCell(
                Strings.proficient,
                data.proficient
            )
            Spacer(modifier = Modifier.height(
                deviceValue(8, 16).dp
            ))
            SkillCell(
                Strings.competent,
                data.competent
            )
            Spacer(modifier = Modifier.height(
                deviceValue(8, 16).dp
            ))
            SkillCell(
                Strings.familiar,
                data.familiar
            )
            Spacer(modifier = Modifier.height(
                deviceValue(8, 16).dp
            ))
        } ?: run {

        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SkillCell(
    title: Int,
    items: List<List<SkillResponse.Entity>>
){
    if (title != 0)
        Text(
            modifier = Modifier
                .padding(bottom = deviceValue(3, 6).dp),
            text = stringResource(title),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = deviceValue(13, 22).sp,
            fontWeight = FontWeight.Medium
        )
    items.forEach { list ->
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(11.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ){
            val height = deviceValue(21, 30).dp
            val size = deviceValue(10, 15).sp
            list.forEach {
                Chip(
                    height = height,
                    size = size,
                    text = it.name
                )
            }
        }
    }
    Spacer(Modifier.height(4.dp))
}