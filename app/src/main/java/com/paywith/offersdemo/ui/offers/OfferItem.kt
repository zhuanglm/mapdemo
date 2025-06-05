package com.paywith.offersdemo.ui.offers

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.transformations
import coil3.size.Size
import coil3.transform.CircleCropTransformation
import com.paywith.offersdemo.ui.model.OfferUiModel
import com.paywith.offersdemo.R
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

    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .clickable { onClick() }
            .padding(vertical = 15.dp)
    ) {
        val (profileImage, merchantName, merchantDesc, pointsFrame, divider) = createRefs()

        val painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(obs.merchantLogoUrl)
                .crossfade(true)
                .size(Size.ORIGINAL)
                .transformations(CircleCropTransformation())
                .build()
        )

        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .size(dimensionResource(id = R.dimen.merchant_profile_size))
                .clip(CircleShape)
                .border(2.dp, Color.Transparent, CircleShape)
                .constrainAs(profileImage) {
                    start.linkTo(parent.start, marginFifteen)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                },
            contentScale = ContentScale.Crop
        )

        Text(
            text = obs.merchantName,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.constrainAs(merchantName) {
                start.linkTo(profileImage.end, marginTen)
                end.linkTo(pointsFrame.start, marginTen)
                top.linkTo(profileImage.top)
                bottom.linkTo(profileImage.bottom)
                verticalBias = 0.32f
                width = Dimension.fillToConstraints
            }
        )

        Text(
            text = obs.distance + " \u2022 " + obs.shortMerchantAddress,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 11.sp,
                color = ColorSearchHint
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.constrainAs(merchantDesc) {
                start.linkTo(profileImage.end, marginTen)
                end.linkTo(pointsFrame.start, marginTen)
                top.linkTo(merchantName.bottom, marginFive)
                width = Dimension.fillToConstraints
            }
        )

        Box(
            modifier = Modifier
                .constrainAs(pointsFrame) {
                    end.linkTo(parent.end, marginTwenty)
                    top.linkTo(profileImage.top)
                    bottom.linkTo(profileImage.bottom)
                }
        ) {
            Image(
                painter = pointsBackgroundPainter ,
                contentDescription = stringResource(R.string.points),
                modifier = Modifier
                    .matchParentSize()
                ,
                contentScale = ContentScale.FillBounds
            )

            Text(
                text = obs.pointsText,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = colorResource(id = R.color.colorPointsBlue),
                modifier = Modifier
                    .align(Alignment.Center)
                    .defaultMinSize(minWidth = 80.dp)
                    .wrapContentWidth(Alignment.CenterHorizontally)

                    .padding(horizontal = 16.dp, vertical = 8.dp)
                ,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        HorizontalDivider(
            modifier = Modifier.constrainAs(divider) {
                start.linkTo(merchantDesc.start)
                end.linkTo(parent.end, margin = marginTwenty)
                bottom.linkTo(parent.bottom)
                width = Dimension.fillToConstraints
            },
            thickness = 1.dp,
            color = colorResource(id = R.color.colorDivider)
        )
    }
}
