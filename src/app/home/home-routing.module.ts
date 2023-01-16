import { NgModule } from '@angular/core'
import { Routes } from '@angular/router'
import {NativeScriptRouterModule, NSEmptyOutletComponent} from '@nativescript/angular'
import {HomeComponent} from "./home.component";


const routes: Routes = [
  {
    path: 'base',
    component:HomeComponent,
    children:[
      {
        path: 'favorite',
        loadChildren: () => import('~/app/home/favorite/favorite.module').then((m) => m.FavoriteModule),
        outlet: 'favorite_outlet',
      },
      {
        path: 'search',
        loadChildren: () => import('~/app/home/search/search.module').then((m) => m.SearchModule),
        outlet: 'search_outlet',
      },
      {
        path: 'new',
        loadChildren: () => import('~/app/home/new/new.module').then((m) => m.NewModule),
        outlet: 'new_outlet',
      }
    ]
  },
]

@NgModule({
  imports: [NativeScriptRouterModule.forChild(routes)],
  exports: [NativeScriptRouterModule],
})
export class HomeRoutingModule {}
