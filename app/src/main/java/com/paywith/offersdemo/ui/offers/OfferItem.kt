package com.paywith.offersdemo.ui.offers

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.paywith.offersdemo.R
import com.paywith.offersdemo.ui.model.OfferUiModel
import com.paywith.offersdemo.ui.theme.ColorSearchHint

@Composable
fun OfferItem(
    modifier: Modifier = Modifier,
    obs: OfferUiModel,
    onClick: () -> Unit
) {
    val marginFive = dimensionResource(id = R.dimen.margin_five)
    val marginTen = dimensionResource(id = R.dimen.margin_ten)
    val marginFifteen = dimensionResource(id = R.dimen.margin_fifteen)
    val marginTwenty = dimensionResource(id = R.dimen.margin_twenty)
    val pointsBackgroundPainter = painterResource(id = R.drawable.rect_rounded_blue_pale)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 15.dp)
                .padding(start = marginFifteen, end = marginTwenty),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = obs.merchantLogoUrl,
                contentDescription = obs.merchantName,
                placeholder = painterResource(id = R.drawable.ic_shoppingbag),
                error = painterResource(id = R.drawable.shoppingbag),
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.merchant_profile_size))
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(marginTen))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = obs.merchantName,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(marginFive))

                Text(
                    text = "${obs.distance} \u2022 ${obs.shortMerchantAddress}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        color = ColorSearchHint
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(marginTen))

            Box(
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = pointsBackgroundPainter,
                    contentDescription = null,
                    modifier = Modifier.matchParentSize(),
                    contentScale = ContentScale.FillBounds
                )
                Text(
                    text = obs.pointsText,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = colorResource(id = R.color.colorPointsBlue),
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    maxLines = 1
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = marginFifteen + dimensionResource(id = R.dimen.merchant_profile_size) + marginTen, end = marginTwenty),
            thickness = 1.dp,
            color = colorResource(id = R.color.colorDivider)
        )
    }
}