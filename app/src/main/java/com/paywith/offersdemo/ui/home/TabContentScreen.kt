package com.paywith.offersdemo.ui.home

import android.content.Context
import android.content.Intent
import android.location.Location
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.paywith.offersdemo.R
import com.paywith.offersdemo.ui.model.OfferUiModel
import com.paywith.offersdemo.ui.theme.LightBlue
import com.paywith.offersdemo.ui.theme.LightGrey

@Composable
fun OfferTabContent(
    offerPoints: String,
    offerDetails: String
) {
    val marginFive = dimensionResource(id = R.dimen.margin_five)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Merchant Icon
        Image(
            painter = painterResource(id = R.drawable.ic_shoppingbag),
            contentDescription = "Merchant Icon",
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(50.dp)
        )

        // Offer Points
        Text(
            text = offerPoints,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier
                .padding(top = marginFive)
        )

        // Offer Description
        Text(
            text = offerDetails,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = marginFive)
                .fillMaxWidth(0.65f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OfferTabContentPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(Color.White)
                .padding(16.dp)
        ) {
            OfferTabContent(
                offerPoints = "5x pts",
                offerDetails = "Get 5x points for every purchase"
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ProfileTabContentPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(Color.White)
                .padding(16.dp)
        ) {
            ProfileTabContent(null)
        }
    }
}

@Composable
fun ProfileTabContent(profile: OfferUiModel?) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // About section
        Row(
            modifier = Modifier
                .padding(horizontal = 30.dp, vertical = 30.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_info),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(30.dp))
            Column {
                Text(
                    text = stringResource(id = R.string.about),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = profile?.merchantDescription.orEmpty(),
                    color = LightGrey,
                    fontSize = 13.sp
                )
            }
        }

        // Address section
        Row(
            modifier = Modifier
                .padding(horizontal = 30.dp, vertical = 10.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_map_blue),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(30.dp))
            Column {
                Text(
                    text = stringResource(id = R.string.address),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = profile?.merchantAddress.orEmpty(),
                    color = Color(0xFF6E6E6E),
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    modifier = Modifier.clickable {
                        profile?.merchantLocation?.let { location ->
                            val gmmIntentUri =
                                "google.navigation:q=${location.latitude},${location.longitude}".toUri()
                            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                                setPackage("com.google.android.apps.maps")
                            }
                            context.startActivity(mapIntent)
                        }
                    },
                    text = stringResource(id = R.string.get_directions),
                    color = LightBlue,
                    fontSize = 13.sp
                )
            }
        }

        // Phone section
        if (profile?.merchantPhoneNumber?.isNotEmpty() == true) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 30.dp, vertical = 30.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_phone),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(30.dp))
                Column {
                    Text(
                        text = stringResource(id = R.string.phone),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        modifier = Modifier.clickable {
                            profile.merchantPhoneNumber.let {
                                val intent = Intent(Intent.ACTION_DIAL)
                                intent.data = "tel:$it".toUri()
                                context.startActivity(intent)
                            }
                        },
                        text = profile.merchantPhoneNumber,
                        color = LightBlue,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // Website section
        if (profile?.merchantWebsite?.isNotEmpty() == true) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 30.dp, vertical = 30.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_laptop),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(30.dp))
                Column {
                    Text(
                        text = stringResource(id = R.string.website),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        modifier = Modifier.clickable {
                            openUrl(context, profile.merchantWebsite)
                        },
                        text = profile.merchantWebsite,
                        color = LightBlue,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // Online Ordering section
        if (profile?.onlineOrderingLink?.isNotEmpty() == true) {
            SocialRow(context, R.drawable.ic_laptop, R.string.online_ordering,
                profile.onlineOrderingLink)
        }
        // Social Links
        if (profile?.facebookPageLink?.isNotEmpty() == true) {
            SocialRow(context, R.drawable.ic_facebook, R.string.facebook,
                profile.facebookPageLink)
        }
        if (profile?.twitterPageLink?.isNotEmpty() == true) {
            SocialRow(context, R.drawable.ic_twitter, R.string.twitter,
                profile.twitterPageLink)
        }
        if (profile?.instagramPageLink?.isNotEmpty() == true) {
            SocialRow(context, R.drawable.ic_instagram, R.string.instagram,
                profile.instagramPageLink)
        }

        // Map container
        profile?.merchantLocation?.let { StaticGoogleMap(it) }
    }
}

@Composable
private fun SocialRow(context: Context, iconRes: Int, textRes: Int, url: String) {
    Row(
        modifier = Modifier
            .padding(horizontal = 30.dp, vertical = 30.dp)
            .clickable {
                openUrl(context, url)
            },
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(30.dp))
        Text(
            text = stringResource(id = textRes),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun StaticGoogleMap(location: Location) {
    val latLng = LatLng(location.latitude, location.longitude)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(latLng, 16f)
    }

    GoogleMap(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(30.dp),
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,
            zoomGesturesEnabled = false,
            scrollGesturesEnabled = false,
            tiltGesturesEnabled = false,
            scrollGesturesEnabledDuringRotateOrZoom = false
        )
    ) {
        Marker(
            state = MarkerState(position = latLng),
            title = "Marker",
            snippet = "Static Location"
        )
    }
}

private fun openUrl(context: Context, url: String) {
    val i = Intent(Intent.ACTION_VIEW)
    i.data = url.toUri()
    context.startActivity(i)
}