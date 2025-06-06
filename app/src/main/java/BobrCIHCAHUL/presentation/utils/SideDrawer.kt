package BobrCIHCAHUL.presentation.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import BobrCIHCAHUL.presentation.userinfo.UserPicture
import BobrCIHCAHUL.presentation.task.TaskListViewModel
import BobrCIHCAHUL.presentation.userinfo.UserViewModel
import BobrCIHCAHUL.ui.theme.MainBackground
import BobrCIHCAHUL.ui.theme.interBold
import BobrCIHCAHUL.ui.theme.interLight

@Composable
fun SideDrawer(
    navController: NavController,
    userViewModel : UserViewModel = viewModel(),
    taskListViewModel: TaskListViewModel = viewModel(modelClass = TaskListViewModel::class.java),
) {

    Row(modifier = Modifier
        .shadow(2.dp)
        .fillMaxWidth()
        .height(200.dp)
        .background(MainBackground)
        .padding(PaddingValues(top = 10.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center){

        UserPicture(size = 70)

        Column(modifier = Modifier.padding(PaddingValues(start = 10.dp))) {
            Text(text = userViewModel.userName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = interBold
            )

            Text(text = "${taskListViewModel.getUnfinishedTasks().size} unfinished tasks\n${taskListViewModel.getFinishedTasks().size} finished tasks",
                fontSize = 10.sp,
                fontWeight = FontWeight.Light,
                fontFamily = interLight
            )
        }
    }

    SignOutButton(navController)
}
