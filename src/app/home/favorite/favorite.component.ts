import {Component, OnInit} from '@angular/core';
import {Page} from "@nativescript/core";

@Component({
  selector: 'app-favorite',
  templateUrl: './favorite.component.html',
  styleUrls: ['./favorite.component.scss']
})
export class FavoriteComponent implements OnInit{
  data='';
  constructor(private page:Page) {
  }
  ngOnInit(){
    this.page.actionBarHidden=true
  }
  async setData(){
    // const value = await this.getData()
    // this.data = JSON.stringify(value);
  }
  // getData():Promise<string>{
  //   return new Promise((resolve, reject) => {
  //     const url = 'http://54.157.186.34:3000/ahoj';
  //     let xhr = new XMLHttpRequest()
  //     xhr.onreadystatechange = function () {
  //       if (xhr.readyState === 4) {
  //         if (xhr.status === 200) {
  //           resolve(JSON.parse(xhr.response))
  //         } else {
  //           reject(xhr.response)
  //         }
  //       }
  //     }
  //     xhr.open("POST", url, true)
  //     xhr.send()
  //   })
  // }
}
