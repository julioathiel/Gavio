package com.gastosdiarios.gavio.presentation.home.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.presentation.home.HomeViewModel

@Composable
fun HorizontalPagerWithCards(viewModel: HomeViewModel, modifier: Modifier) {
    val listFilter = viewModel.listFilter
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { listFilter.size })

    if (listFilter.isNotEmpty()) {
        HorizontalPager(
            state = pagerState,
            modifier = modifier,
            contentPadding = PaddingValues(horizontal = 30.dp),
            pageSpacing = 10.dp,
            pageContent = { pageIndex ->
                val item = listFilter[pageIndex]
                key(item.uid) {
                    CardListItem(
                        modifier = modifier,
                        viewModel = viewModel,
                        item = item,
                        onPagarItem = {
                            viewModel.pagarItem(item)
                        },
                        onRemoveItem = { viewModel.clearItem(item) }
                    )
                }
            }
        )
    }
}
