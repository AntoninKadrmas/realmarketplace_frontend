import { NgModule, NO_ERRORS_SCHEMA } from '@angular/core'
import { NativeScriptCommonModule, NativeScriptRouterModule } from '@nativescript/angular'

import {SearchComponent} from "./search.component";
import {ItemComponent} from "../item/item.component";
import {ItemContentComponent} from "../item_content/itemContent.component";
import {PictureSlideComponent} from "../item_content/pictureSlide/pictureSlide.component";

@NgModule({
    imports: [
        NativeScriptCommonModule,
        NativeScriptRouterModule.forChild([{
            path: '',
            component: SearchComponent,
        }]),
        NativeScriptRouterModule
    ],
    declarations: [
        SearchComponent,
        ItemComponent,
        ItemContentComponent,
        PictureSlideComponent
    ],
    schemas: [NO_ERRORS_SCHEMA],
    exports: [
        ItemComponent
    ]
})
export class SearchModule {}
