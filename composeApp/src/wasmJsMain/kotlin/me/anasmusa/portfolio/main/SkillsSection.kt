package me.anasmusa.portfolio.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.valentinilk.shimmer.shimmer
import me.anasmusa.portfolio.api.model.SkillResponse
import me.anasmusa.portfolio.component.Chip
import me.anasmusa.portfolio.component.ShimmerBox
import me.anasmusa.portfolio.component.Title
import me.anasmusa.portfolio.core.deviceValue
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import portfolio.composeapp.generated.resources.*

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

        Title(
            icon = Res.drawable.ic_hash,
            title = Res.string.skills
        )

        data?.let { data ->
            SkillCell(
                Res.string.proficient,
                data.proficient
            )
            Spacer(modifier = Modifier.height(
                deviceValue(8, 16).dp
            ))
            SkillCell(
                Res.string.competent,
                data.competent
            )
            Spacer(modifier = Modifier.height(
                deviceValue(8, 16).dp
            ))
            SkillCell(
                Res.string.familiar,
                data.familiar
            )
            Spacer(modifier = Modifier.height(
                deviceValue(8, 16).dp
            ))
        } ?: run {
            ShimmerSkillCell(Res.string.proficient)
            Spacer(modifier = Modifier.height(
                deviceValue(8, 16).dp
            ))
            ShimmerSkillCell(Res.string.competent)
            Spacer(modifier = Modifier.height(
                deviceValue(8, 16).dp
            ))
            ShimmerSkillCell(Res.string.familiar)
            Spacer(modifier = Modifier.height(
                deviceValue(8, 16).dp
            ))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SkillCell(
    title: StringResource,
    items: List<List<SkillResponse.Entity>>
){
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
                    text = it.name,
                    filled = false
                )
            }
        }
        Spacer(Modifier.height(4.dp))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ShimmerSkillCell(title: StringResource){
    Text(
        modifier = Modifier
            .padding(bottom = deviceValue(3, 6).dp),
        text = stringResource(title),
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = deviceValue(13, 22).sp,
        fontWeight = FontWeight.Medium
    )
    val verticalScape = deviceValue(4, 8).dp
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .shimmer(),
        horizontalArrangement = Arrangement.spacedBy(11.dp),
        verticalArrangement = Arrangement.spacedBy(verticalScape)
    ){
        val height = deviceValue(21, 30).dp
        repeat(5){
            ShimmerBox(
                modifier = Modifier
                    .width(
                        deviceValue(60, 150).dp
                    )
                    .height(height),
                shape = RoundedCornerShape(height/2)
            )
        }
        Spacer(Modifier.height(verticalScape))
    }
}